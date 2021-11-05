package eu.europa.ec.empl.edci.repository.mapper;

import eu.europa.ec.empl.edci.mapper.annotations.RuntimeMapping;
import eu.europa.ec.empl.edci.mapper.annotations.RuntimeMappings;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class InputsParser {

    static final Logger logger = Logger.getLogger(InputsParser.class);

    public static Pageable parseViewSortFields(Pageable pageable, Object mapper) {

        Pageable pageableaux = pageable;

        try {

            Iterator<Sort.Order> it = pageable.getSort().iterator();
            RuntimeMappings ms = AnnotationUtils.findAnnotation(mapper.getClass().getMethod("toVO", Object.class), RuntimeMappings.class);
            RuntimeMapping[] m = ms.value();

            List<Sort.Order> orders = new ArrayList<>();

            while (it.hasNext()) {

                Sort.Order ord = it.next();
                for (String s : ord.getProperty().split(",")) {
                    orders.add(
                            new Sort.Order(
                                    ord.getDirection(),
                                    Arrays.stream(m).filter(aux -> s.startsWith(aux.target())).findFirst()
                                            .map(aux -> s.replaceFirst(aux.target(), aux.source())).orElse(s)));
                }

            }

            pageableaux = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return pageableaux;
    }

    public static String parseViewSearchFields(String search, Object mapper) {

        String searchaux = search;

        try {

            if (mapper != null) {
                RuntimeMappings ms = AnnotationUtils.findAnnotation(mapper.getClass().getMethod("toVO", Object.class), RuntimeMappings.class);
                RuntimeMapping[] m = ms.value();

                searchaux = Arrays.stream(m).filter(aux -> search.startsWith(aux.target())).findFirst()
                        .map(aux -> search.replaceFirst(aux.target(), aux.source())).orElse(search);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return searchaux;
    }
}
