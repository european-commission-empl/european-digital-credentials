package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIConflictException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("ResourcesUtil")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ResourcesUtil {

    public static final Logger logger = LogManager.getLogger(ResourcesUtil.class);

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param root       The root node
     * @param getRelElem Function that will be called to get the related object of the Root element
     * @param fieldName  Name of the relation beeing checked (Only used to inform the user)
     * @param <T>        The Root base class
     */
    public <T> void checkLoopLine(T root, Function<T, T> getRelElem, String fieldName) {
        checkLoopTree(new HashSet<Integer>(), root, (r) -> new ArrayList<T>() {{
            add(getRelElem.apply(r));
        }}, fieldName);
    }

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param root        The root node
     * @param getRelElems Function that will be called to get the related objects of the Root element
     * @param fieldName   Name of the relation beeing checked (Only used to inform the user)
     * @param <T>         The Root base class
     */
    public <T> void checkLoopTree(T root, Function<T, Collection<T>> getRelElems, String fieldName) {
        checkLoopTree(new HashSet<Integer>(), root, getRelElems, fieldName);
    }

    protected <T> void checkLoopTree(Set<Integer> orgPkList, T elem, Function<T, Collection<T>> getRelElems, String fieldName) {

        if (elem == null) {
            return;
        }

        if (!orgPkList.add(elem.hashCode())) {
            logger.error("A loop was found for with the relation " + fieldName + "-> hashCode(" +
                    orgPkList.stream().map(String::valueOf).collect(Collectors.joining(",")) + "," + elem.hashCode() + ")");
            throw new EDCIConflictException("exception.client.error.msg.builder.conflict", fieldName);
        }

        if (getRelElems.apply(elem) != null) {
            for (T orgAux : getRelElems.apply(elem)) {
                checkLoopTree(orgPkList, orgAux, getRelElems, fieldName);
            }
        }

        orgPkList.remove(elem.hashCode());

    }

    /**
     * Function to check if there's a loop within the relations of an object. If one loop is found a ConflictException (Runtime exception) will be thrown
     *
     * @param elem The root node
     */
    //TODO -> RESTORE WHEN IDENTIFIABLES ARE AVAILABLE
    public <S extends Identifiable> void checkContentClassLoopTree(S elem) {
        checkContentClassLoopTree(new LinkedHashMap<String, String>(), elem);
    }

    protected <S extends Identifiable> void checkContentClassLoopTree(LinkedHashMap<String, String> orgPkList, S elem) {

        if (elem == null || elem.getId() == null) {
            return;
        }

        if (orgPkList.containsKey(elem.getId().toString())) {

            List<String> loopElems = new ArrayList<>();
            boolean found = false;
            for (String id : orgPkList.keySet()) {
                if (found) {
                    loopElems.add(orgPkList.get(id));
                } else if (id.equals(elem.getId().toString())) {
                    found = true;
                    loopElems.add(elem.getId().toString());
                }
            }

            throw new EDCIConflictException("exception.client.error.msg.builder.conflict.full.loop", "'" + elem.getId().toString() + "'",
                    loopElems.stream().collect(Collectors.joining(" -> ", "'", "'")) + " -> '" + elem.getId().toString() + "')");

        } else if (elem.getId() != null) {
            orgPkList.put(elem.getId().toString(), elem.getName());
        }

        List<Identifiable> relElems = findAllObjectsIn(elem, Identifiable.class);

        if (relElems != null && !relElems.isEmpty()) {
            for (Identifiable orgAux : relElems) {
                checkContentClassLoopTree(orgPkList, orgAux);
            }
        }

        orgPkList.remove(elem.getId().toString());

    }

    public <T> List<T> getCollectionFromMethod(Object root, Class<T> type, Method m) {

        List<T> elems = new ArrayList<>();

        try {

            Collection col = (Collection) m.invoke(root);


            if (col != null && !col.isEmpty()) {

                if (col.stream().anyMatch(c -> type.isInstance(c))) {
                    elems.addAll(col);
                }
            }

        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        }

        return elems;

    }

    public <T> T getObjectFromMethod(Object root, Class<T> type, Method m) {

        T ident = null;

        try {

            ident = (T) m.invoke(root);

        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        }

        return ident;

    }

    /**
     * Searches inside the root object for any references to another objects of the class passed by parameter
     *
     * @param root Root object
     * @param type Class of the objects that will be searched inside root
     * @param <T>  Type of objects that will be returned
     * @return A list of Objects instances of T that are found inside the object root
     */
    public <T> List<T> findAllObjectsIn(Object root, Class<T> type) {

        List<T> elems = new ArrayList<>();

        List<Method> mListCol = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && Collection.class.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());

        List<Method> mListElem = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && m.getAnnotation(XmlTransient.class) == null
                        && type.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());


        for (Method m : mListCol) {
            elems.addAll(getCollectionFromMethod(root, type, m));
        }

        for (Method m : mListElem) {
            T obj = getObjectFromMethod(root, type, m);
            if (obj != null) {
                elems.add(obj);
            }
        }

        return elems;
    }

    public <T> List<T> findAllObjectsInRecursivily(Object root, Class<T> searchElems, FieldFilter<T> filter, Class... pathElems) {
        List<T> identifiebleObj = new ArrayList<>();
        findAllObjectsInRecursivily(root, identifiebleObj, searchElems, filter, pathElems);
        return identifiebleObj;
    }

    public <T> List<T> findAllObjectsInRecursivily(Object root, Class<T> searchElems, Class... pathElems) {
        List<T> identifiebleObj = new ArrayList<>();
        findAllObjectsInRecursivily(root, identifiebleObj, searchElems, null, pathElems);
        return identifiebleObj;
    }

    /* Warning, the result list is the auxList, not the return*/
    protected <T, R> List<R> findAllObjectsInRecursivily(Object root, List<T> auxField, Class<T> searchElems, FieldFilter<T> filter, Class<R>... pathElems) {

        List<R> identifiebleObj = new ArrayList<>();

        for (Class<R> pathElem : pathElems) {
            identifiebleObj.addAll(findAllObjectsIn(root, pathElem));
        }

        for (R aux : identifiebleObj) {
            identifiebleObj = findAllObjectsInRecursivily(aux, auxField, searchElems, filter, pathElems);
        }

        List<T> foundElems = findAllObjectsIn(root, searchElems);

        for (T elem : foundElems) {
            if (filter == null) {
                auxField.add(elem);
            } else if (filter.filter(elem)) {
                auxField.add(elem);
            }
        }

        return identifiebleObj;

    }

    public interface FieldFilter<T> {
        boolean filter(T element);
    }

    public <T> List<Method> findAllMethodsIn(Object root, Class<T> type) {

        List<Method> elems = new ArrayList<>();

        List<Method> mListCol = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && Collection.class.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());

        List<Method> mListElem = Arrays.stream(root.getClass().getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && type.isAssignableFrom(m.getReturnType()))
                .collect(Collectors.toList());

        elems.addAll(mListCol);
        elems.addAll(mListElem);

        return elems;
    }

    public <T> Map<Method, Set<Object>> findAllMethodsInRecursivily(Object root, Class<T> searchElems, Class... pathElems) {
        Map<Method, Set<Object>> identifiebleObj = new HashMap<>();
        findAllMethodsInRecursivily(root, identifiebleObj, searchElems, pathElems);
        return identifiebleObj;
    }

    /* Warning, the result list is the auxList, not the return*/
    protected <T, R> List<R> findAllMethodsInRecursivily(Object root, Map<Method, Set<Object>> auxField, Class<T> searchElems, Class<R>... pathElems) {

        List<R> identifiebleObj = new ArrayList<>();

        for (Class<R> pathElem : pathElems) {
            identifiebleObj.addAll(findAllObjectsIn(root, pathElem));
        }

        for (R aux : identifiebleObj) {
            identifiebleObj = findAllMethodsInRecursivily(aux, auxField, searchElems, pathElems);
        }

        List<Method> foundElems = findAllMethodsIn(root, searchElems);

        for (Method elem : foundElems) {
            if (!auxField.containsKey(elem)) {
                auxField.put(elem, new HashSet<>());
            }
            auxField.get(elem).add(root);
        }

        return identifiebleObj;

    }

}
