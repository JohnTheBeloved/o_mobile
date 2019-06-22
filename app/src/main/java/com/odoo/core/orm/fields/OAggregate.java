package com.odoo.core.orm.fields;


public class OAggregate {

    private String name, label;

    public enum Operation {

        SUM("SUM"),
        COUNT("COUNT"),
        COUNT_DISTINCT("COUNT(DISTINCT ", 2),
        DATE("DATE");
        private final String symbol;
        private final int noOfClosingBrackets;

        Operation(String symbol){
            this(symbol, 1);
        }

        Operation(String symbol, int noOfClosingBrackets){
            this.symbol = symbol;
            this.noOfClosingBrackets = noOfClosingBrackets;
        }

        public String syntax(String col) {
            return symbol + "(" + col + String.format("%"+ noOfClosingBrackets +"c", ')').replace(" ", ")");
        }
    }

    private String column;
    private Operation operation;
    private String alias;

    public OAggregate(String column,Operation operation){
        this(column,operation, null);
    }

    public OAggregate(String oColumn, Operation operation, String alias){
        this.column = oColumn;
        this.operation = operation;
        this.alias = alias == null ? column : alias;
    }

    public String getSyntax(){
        return getOperation().syntax(column) + " as " + alias;
    }

    @Override
    public String toString() {
        return operation.syntax(column);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setoColumn(String column) {
        this.column = column;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getoColumn() {
        return column;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getAlias() {
        return alias;
    }
}
