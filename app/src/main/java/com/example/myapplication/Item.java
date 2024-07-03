// Item.java
package com.example.myapplication;

public class Item implements Comparable<Item> {
    private int id;
    private int listId;
    private String name;

    public Item(int id, int listId, String name) {
        this.id = id;
        this.listId = listId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Item other) {
        int listIdCompare = Integer.compare(this.listId, other.listId);
        if (listIdCompare == 0) {
            return this.name.compareTo(other.name);
        }
        return listIdCompare;
    }
}
