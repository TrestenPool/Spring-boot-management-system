package mvc.models;

import java.util.ArrayList;

public class PagingResults {

    private ArrayList<Person> values;
    private int offset;
    private int pageNumber;
    private int pageSize;
    private boolean isLast;
    private int totalElements;
    private int size;
    private int number;
    private int numberOfElements;
    private int totalPages;


    public PagingResults() { }

    public ArrayList<Person> getValues() {
        return values;
    }

    public void setValues(ArrayList<Person> values) {
        this.values = values;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
    public int getNumberOfElements() {
        return numberOfElements;
    }
}