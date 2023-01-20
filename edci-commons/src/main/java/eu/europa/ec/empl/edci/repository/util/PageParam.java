//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package eu.europa.ec.empl.edci.repository.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Objects;

@JsonNaming(value = PropertyNamingStrategy.LowerCaseStrategy.class)
public class PageParam {

    private String sort;

    private String direction;

    private int page;

    private int size;

    public final static int SIZE_PAGE_DEFAULT = 10;

    public enum NavAction {
        ALL,
        FIRST,
        PREV,
        NEXT,
        LAST
    }

    public PageParam() {

    }

    public PageParam(int page, int size) {
        this.sort = sort;
        this.direction = direction;
        this.page = page;
        this.size = size;
    }

    public PageParam(int page, int size, String sort, String direction) {
        this.sort = sort;
        this.direction = direction;
        this.page = page;
        this.size = size;
    }

    public PageRequest toPageRequest() {
        if (StringUtils.isEmpty(sort)) {
            return PageRequest.of(this.getPage(), this.getSize() > 0 ? this.getSize() : SIZE_PAGE_DEFAULT);
        } else {
            return PageRequest.of(this.getPage(), this.getSize() > 0 ? this.getSize() : SIZE_PAGE_DEFAULT,
                    Sort.Direction.fromOptionalString(this.getDirection()).orElse(null), sort != null ? sort.split(",") : null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageParam that = (PageParam) o;
        return page == that.page &&
                size == that.size &&
                sort.equals(that.sort) &&
                direction.equals(that.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sort, direction, page, size);
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String toString() {
        return String.format("Page request [number: %d, size %d, sort: %s, direction]",
                this.getPage(),
                this.getSize(),
                this.getSort() == null ? null : this.getSort().toString(),
                this.getDirection());
    }
}
