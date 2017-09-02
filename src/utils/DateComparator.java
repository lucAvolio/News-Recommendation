package utils;

import java.util.Comparator;

import model.Entity;
//Compare entities by count
public class DateComparator implements Comparator<Entity> {
    public int compare(Entity a, Entity b) {
        return a.getDate().compareTo(b.getDate());
    }
}