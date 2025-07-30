package br.edu.ifba.inf008.core.ui.components.table.factories;

import br.edu.ifba.inf008.core.domain.interfaces.Nameable;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnOrientation;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnSize;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableLabel;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;

public class TableColumnFactory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "dd/MM/yyyy");

    /**
     * Creates a TableColumn for the given method. This method uses reflection to invoke the method
     * on the table item instances.
     *
     * @param method the method to create the column for
     * @param <T>    the type of the table items
     * @return a TableColumn for the specified method
     */
    public static <T> TableColumn<T, String> getTStringTableColumn(Method method) {
        String headerName = method.getAnnotation(TableLabel.class).value();
        Function<T, Object> valueProvider = instance -> {
            try {
                return method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        };
        double width = method.isAnnotationPresent(TableColumnSize.class)
                ? method.getAnnotation(TableColumnSize.class).value() : 150;
        Pos alignment = method.isAnnotationPresent(TableColumnOrientation.class)
                ? method.getAnnotation(TableColumnOrientation.class).value() : Pos.CENTER_LEFT;

        return createColumn(headerName, valueProvider, width, alignment);
    }

    /**
     * Creates a TableColumn for the given field.
     *
     * @param field      the field to create the column for
     * @param headerName the header name for the column
     * @param <T>        the type of the table items
     * @return a TableColumn for the specified field
     */
    public static <T> TableColumn<T, String> getTStringTableColumn(Field field, String headerName) {
        Function<T, Object> valueProvider = instance -> {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                return null;
            }
        };
        double width = field.isAnnotationPresent(TableColumnSize.class)
                ? field.getAnnotation(TableColumnSize.class).value() : 150;
        Pos alignment = field.isAnnotationPresent(TableColumnOrientation.class)
                ? field.getAnnotation(TableColumnOrientation.class).value() : Pos.CENTER_LEFT;

        return createColumn(headerName, valueProvider, width, alignment);
    }

    /**
     * Creates a TableColumn with the specified header name and value provider.
     *
     * @param headerName    the header name for the column
     * @param valueProvider a function that provides the value for each cell in the column
     * @param prefWidth     the preferred width of the column
     * @param alignment     the alignment of the content inside the cells
     * @param <T>           the type of the table items
     * @return a TableColumn with the specified properties
     */
    private static <T> TableColumn<T, String> createColumn(String headerName,
            Function<T, Object> valueProvider,
            double prefWidth,
            Pos alignment) {
        TableColumn<T, String> column = new TableColumn<>(headerName);

        column.setCellFactory(col -> new TableCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(alignment);

                setAlignment(alignment);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    Tooltip tooltip = new Tooltip(item);
                    Tooltip.install(label, tooltip);
                    setGraphic(label);
                }
            }
        });

        column.setCellValueFactory(cellData -> {
            Object value = valueProvider.apply(cellData.getValue());
            String text = formatValue(value);
            return new SimpleStringProperty(text);
        });

        column.setMinWidth(prefWidth * 0.8);
        column.setPrefWidth(prefWidth);
        column.setMaxWidth(prefWidth * 1.2);

        return column;
    }

    /**
     * Formats the value for display in the table cell.
     *
     * @param value the value to format
     * @return a formatted string representation of the value
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        return switch (value) {
            case LocalDateTime dateTime -> dateTime.format(DATE_TIME_FORMATTER);
            case LocalDate date -> date.format(DATE_FORMATTER);
            case Date date -> new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
            case Nameable nameable -> nameable.getName();
            default -> value.toString();
        };
    }
}
