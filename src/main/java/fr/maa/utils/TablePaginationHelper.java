package fr.maa.utils;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.function.Function;
import java.util.function.Predicate;

public final class TablePaginationHelper {

    private TablePaginationHelper() {
    }

    public static <T> void setup(TableView<T> table,
                                 TextField searchField,
                                 Pagination pagination,
                                 ObservableList<T> data,
                                 Function<String, Predicate<T>> predicateFactory,
                                 int pageSize) {

        FilteredList<T> filtered = new FilteredList<>(data, predicateFactory.apply(""));
        SortedList<T> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());

        Runnable updatePagination = () -> {
            int total = sorted.size();
            int pageCount = Math.max(1, (int) Math.ceil((double) total / pageSize));
            pagination.setPageCount(pageCount);
            int currentPage = Math.min(pagination.getCurrentPageIndex(), pageCount - 1);
            pagination.setCurrentPageIndex(currentPage);
            updateTableItems(table, sorted, currentPage, pageSize);
        };

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String query = newValue == null ? "" : newValue.trim().toLowerCase();
            filtered.setPredicate(predicateFactory.apply(query));
        });

        filtered.addListener((ListChangeListener<T>) change -> updatePagination.run());
        table.comparatorProperty().addListener((obs, oldComp, newComp) ->
                updateTableItems(table, sorted, pagination.getCurrentPageIndex(), pageSize));
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                updateTableItems(table, sorted, newIndex.intValue(), pageSize));
        pagination.setPageFactory(pageIndex -> {
            updateTableItems(table, sorted, pageIndex, pageSize);
            return new Pane();
        });

        updatePagination.run();
    }

    private static <T> void updateTableItems(TableView<T> table, SortedList<T> sorted, int pageIndex, int pageSize) {
        int fromIndex = pageIndex * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, sorted.size());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }
        table.setItems(FXCollections.observableArrayList(sorted.subList(fromIndex, toIndex)));
    }
}
