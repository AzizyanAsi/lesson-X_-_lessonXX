package generective.service;

import generective.models.Basket;
import generective.models.Group;
import generective.models.Item;

import java.util.*;

public class Storage {
    private static List<Group> roots = new ArrayList<>();
    public static Basket basket = new Basket();
    public static List<Group> groups = new ArrayList<>();

    public static void addToRoots(Group group) {
        roots.add(group);
    }

    public static boolean addGroup(Group addedGroup, String id, List<Group> groups, boolean addToRoot) {
        boolean isAdded = false;
        List<Group> copyGroup = new ArrayList<>(groups);
        Group parent;
        Iterator<Group> iterator = copyGroup.iterator();
        while (iterator.hasNext()) {
            parent = iterator.next();
            if (parent.getId().equals(id)) {
                parent.getGroups().add(addedGroup);//parent group add sub
                generective.service.Storage.groups.add(addedGroup);
                addedGroup.setGroupParent(parent);//sub group add parent
                return true;
            } else {
                isAdded = addGroup(addedGroup, id, parent.getGroups(), false);
                if (isAdded) return true;
            }
        }

        if (addToRoot && !isAdded) {
            generective.service.Storage.groups.add(addedGroup);
            roots.add(addedGroup);
        }

        return false;
    }

    public static boolean addItem(Item addedItem, String id, List<Group> groups) {
        boolean isAdded = false;
        List<Group> copyGroup = new ArrayList<>(groups);
        Group g;
        Iterator<Group> iterator = copyGroup.iterator();
        while (iterator.hasNext()) {
            g = iterator.next();
            if (g.getId().equals(id)) {
                g.getItems().add(addedItem);
                addedItem.setParentGroup(g);
                return true;
            } else {
                isAdded = addItem(addedItem, id, g.getGroups());
                if (isAdded) return true;
            }
        }
        return false;
    }

    public static Optional<Group> findGroupByName(String name) {
        return groups.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst();
    }

    public static Item findItemByTitle(String name) {
        for (Group group : groups) {
            Optional<Item> item = group.getItems()
                    .stream()
                    .filter(b -> b.getName().equals(name))
                    .findFirst();
            if (item.isPresent())
                return item.get();
        }
        return null;
    }

    public static Optional<Item> findHighestPricedItemInTheDirectGroup(Group group) {
        return group.getItems()
                .stream()
                .max(Comparator.comparing(Item::getPrice));

    }

    public static Optional<Item> findHighestPricedItem() {
        List<Item> maxes = new ArrayList<>();
        for (Group group : groups) {
            Optional<Item> item = group.getItems()
                    .stream()
                    .max(Comparator.comparing(Item::calculatePrice));
            item.ifPresent(maxes::add);
        }

        return maxes.stream()
                .max(Comparator.comparing(Item::calculatePrice));

    }

    public static Double findAveragePriceRateInTheGroup(Group group) {
        return group.getItems()
                .stream()
                .mapToDouble(Item::calculatePrice)
                .average()
                .orElse(Double.NaN);
    }

    public static List<Group> getRoots() {
        return roots;
    }
}