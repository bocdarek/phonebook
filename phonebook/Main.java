package phonebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private final static String directoryPath = "/home/bocd/Downloads/directory.txt";
    private final static String findPath = "/home/bocd/Downloads/find.txt";

    private static List<Contact> peopleContacts = new ArrayList<>();
    private static List<Contact> peopleContactsBackUp;
    private final static List<String> peopleToFind = new ArrayList<>();
    private final static List<String> foundNumbers = new ArrayList<>();

    public static void main(String[] args) {

        loadPeopleContacts();  //Reading phone numbers from file
        loadPeopleToFind();   // Reading people to search from file
        peopleContactsBackUp = new ArrayList<>(peopleContacts);

        // Linear Search
        System.out.println("Start searching (linear search)...");
        long start1 = System.currentTimeMillis();
        int counter1 = search(new LinearSearch());
        long stop1 = System.currentTimeMillis();
        long searchTime1 = stop1 - start1;
        System.out.printf("Found %d / %d entries. Time taken: %s%n",
                counter1, peopleToFind.size(), formatTime(searchTime1));

        // Bubble Sort + Jump search
        long maxSortingTime = searchTime1 * 10;
        System.out.println("\nStart searching (bubble sort + jump search)...");
        sortAndSearch(new BubbleSort(), new JumpSearch(), maxSortingTime);

        //Quick Sort + Binary Search
        System.out.println("\nStart searching (quick sort + binary search)...");
        sortAndSearch(new QuickSort(), new BinarySearch());

        //HashTable
        System.out.println("\nStart searching (hash table)...");
        searchWithHashTable();
    }

    private static void loadPeopleContacts() {
        try (Scanner numbersReader = new Scanner(new File(directoryPath))) {
            while (numbersReader.hasNext()) {
                String number = "" + numbersReader.nextLong();
                String name = numbersReader.nextLine().trim();
                peopleContacts.add(new Contact(name, number));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Directory file not found.");
            System.exit(0);
        }
    }

    private static void loadPeopleToFind() {
        try (Scanner peopleReader = new Scanner(new File(findPath))) {
            while (peopleReader.hasNext()) {
                peopleToFind.add(peopleReader.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(0);
        }
    }

    private static int search(Searchable searchAlgorithm) {
        int counter = 0;
        for (String person : peopleToFind) {
            int index = searchAlgorithm.search(peopleContacts, person);
            if (index != -1) {
                foundNumbers.add(peopleContacts.get(index).getNumber());
                counter++;
            }
        }
        return counter;
    }

    private static String formatTime(long timeGap) {
        long minutes = timeGap / (60 * 1000);
        timeGap %= (60 * 1000);
        long seconds = timeGap / 1000;
        long milliSeconds = timeGap % 1000;
        return String.format("%d min. %d sec. %d ms.", minutes, seconds, milliSeconds);
    }

    private static void sortAndSearch(Sortable sortAlgorithm, Searchable searchAlgorithm, long maxTime) {
        peopleContacts = new ArrayList<>(peopleContactsBackUp);
        foundNumbers.clear();
        long start2 = System.currentTimeMillis();
        boolean isSorted = sortAlgorithm.sort(peopleContacts, maxTime);
        long stop2a = System.currentTimeMillis();
        int counter2;
        if (isSorted) {
            counter2 = search(searchAlgorithm);
        } else {
            counter2 = search(new LinearSearch());
        }
        long stop2b = System.currentTimeMillis();
        long sortTime2 = stop2a - start2;
        long searchTime2 = stop2b - stop2a;
        long totalTime2 = stop2b - start2;
        System.out.printf("Found %d / %d entries. Time taken: %s%n",
                counter2, peopleToFind.size(), formatTime(totalTime2));
        System.out.printf("Sorting time: %s%s%n",
                formatTime(sortTime2), isSorted ? "" : " - STOPPED, moved to linear search");
        System.out.printf("Searching time: %s%n", formatTime(searchTime2));
    }

    private static void sortAndSearch(Sortable sortAlgorithm, Searchable searchAlgorithm) {
        sortAndSearch(sortAlgorithm, searchAlgorithm, 0);
    }

    private static void searchWithHashTable() {

        // Hash Table creation
        foundNumbers.clear();
        peopleContacts = new ArrayList<>(peopleContactsBackUp);
        long start = System.currentTimeMillis();
        final Map<String, String> map = new HashMap<>();
        peopleContacts.forEach(contact -> map.put(contact.getName(), contact.getNumber()));
        long stop1 = System.currentTimeMillis();
        long creationTime = stop1 - start;

        // Searching
        int counter = 0;
        for (String person : peopleToFind) {
            String num = map.get(person);
            if (num != null) {
                counter++;
                foundNumbers.add(num);
            }
        }
        long stop2 = System.currentTimeMillis();
        long searchingTime = stop2 - stop1;
        long totalTime = stop2 - start;

        //Summary
        System.out.printf("Found %d / %d entries. Time taken: %s%n",
                counter, peopleToFind.size(), formatTime(totalTime));
        System.out.printf("Creating time: %s%n", formatTime(creationTime));
        System.out.printf("Searching time: %s%n", formatTime(searchingTime));
    }
}
