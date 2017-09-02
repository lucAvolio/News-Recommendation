package utils;

import java.util.Comparator;

import model.Entity;
//Compare entities by count
public class EntityComparator implements Comparator<Entity> {
    public int compare(Entity a, Entity b) {
        if (a.getCount() > b.getCount())
            return 1; // highest value first
        if (a.getCount() == b.getCount())
            return 0;
        return -1;

    }
}
