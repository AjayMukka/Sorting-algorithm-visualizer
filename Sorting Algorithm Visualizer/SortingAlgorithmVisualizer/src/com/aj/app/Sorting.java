package com.aj.app;

import javax.swing.*;
import java.awt.*;

public class Sorting extends JPanel {

    private static final long serialVersionUID = 1L;
    private int[] array;
    private int currentIndex = -1, nextIndex = -1;
    private boolean isPaused = false;
    private long speed = 2000; // Speed in milliseconds
    private JComboBox<String> algorithmSelection;
    private JTextArea inputArray;
    private JTextArea explanationArea; // New JTextArea for step-by-step explanation
    private Thread sortingThread;
    private boolean[] arrows;
    private boolean[] isSorting;
    private boolean[] sorted; // Track sorted elements
    private boolean[] split;  // Track split elements in merge sort
    private boolean[] merge;  // Track merge elements in merge sort

    public Sorting() {
        setPreferredSize(new Dimension(1080, 720)); // Increased height for explanation area
        setBackground(new Color(192, 222, 220));
        setLayout(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 6));

        // Algorithm Selection
        algorithmSelection = new JComboBox<>(new String[]{
                "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"});
        controlPanel.add(algorithmSelection);

        // Input Area for the Array
        inputArray = new JTextArea(1, 20);
        JLabel noteLabel = new JLabel("Input Array\n(comma-separated): ");
        noteLabel.setPreferredSize(new Dimension(30, 30));
        controlPanel.add(noteLabel);
        controlPanel.add(inputArray);

        // Speed Slider

        JSlider speedSlider = new JSlider(1, 1000, 500);
        speedSlider.setPaintLabels(true);
        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setPreferredSize(new Dimension(30, 30));
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.NORTH);

        // Start Button
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100, 30));
        controlPanel.add(startButton);

        // Pause Button
        JButton pauseButton = new JButton("Pause/Resume");
        pauseButton.setPreferredSize(new Dimension(100, 30));
        controlPanel.add(pauseButton);

        // Start Button Action Listener
        startButton.addActionListener(_ -> {
            try {
                String input = inputArray.getText();
                String[] inputStrings = input.split(",");
                array = new int[inputStrings.length];
                for (int i = 0; i < inputStrings.length; i++) {
                    array[i] = Integer.parseInt(inputStrings[i].trim());
                }
                arrows = new boolean[array.length];
                isSorting = new boolean[array.length];
                sorted = new boolean[array.length];
                split = new boolean[array.length];
                merge = new boolean[array.length];
                repaint();
                startSorting();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please enter comma-separated integers.");
            }
        });

        // Pause Button Action Listener
        pauseButton.addActionListener(_ -> isPaused = !isPaused);

        // Speed Slider Action Listener
        speedSlider.addChangeListener(_ -> speed = 1001 - speedSlider.getValue());

        // Explanation Area
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 16));
        explanationArea.setBackground(new Color(240, 240, 240));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        JScrollPane explanationScrollPane = new JScrollPane(explanationArea);
        explanationScrollPane.setPreferredSize(new Dimension(1080, 100));
        add(explanationScrollPane, BorderLayout.SOUTH);
    }

    private void startSorting() {
        if (sortingThread != null && sortingThread.isAlive()) {
            sortingThread.interrupt();
        }
        sortingThread = new Thread(() -> {
            String algorithm = (String) algorithmSelection.getSelectedItem();
            switch (algorithm) {
                case "Bubble Sort":
                    bubbleSort();
                    break;
                case "Selection Sort":
                    selectionSort();
                    break;
                case "Insertion Sort":
                    insertionSort();
                    break;
                case "Merge Sort":
                    mergeSort(array, 0, array.length - 1);
                    break;
                case "Quick Sort":
                    quickSort(array, 0, array.length - 1);
                    break;
            }
            currentIndex = -1;
            nextIndex = -1;
            explanationArea.append("\nSorting completed!\n");
        });
        sortingThread.start();
    }

    private void bubbleSort() {
        explanationArea.append("Starting Bubble Sort...\n");
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                currentIndex = j;
                nextIndex = j + 1;

                arrows[j] = true;
                isSorting[j] = true;
                repaint();
                explanationArea.append("Comparing elements at indices " + j + " and " + (j + 1) + "...\n");
                sleep();

                if (array[j] > array[j + 1]) {
                    explanationArea.append("Swapping elements at indices " + j + " and " + (j + 1) + "...\n");
                    swap(j, j + 1);
                }

                arrows[j] = false;
                isSorting[j] = false;
            }
            sorted[array.length - i - 1] = true;
        }
        repaint();
    }

    private void selectionSort() {
        explanationArea.append("Starting Selection Sort...\n");
        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                currentIndex = j;
                nextIndex = minIndex;

                arrows[j] = true;
                isSorting[j] = true;
                repaint();
                explanationArea.append("Checking if element at index " + j + " is smaller than current minimum...\n");
                sleep();

                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }

                arrows[j] = false;
                isSorting[j] = false;
            }

            if (minIndex != i) {
                explanationArea.append("Swapping elements at indices " + i + " and " + minIndex + "...\n");
                swap(i, minIndex);
            }

            sorted[i] = true;
        }
        sorted[array.length - 1] = true;
        repaint();
    }

    private void insertionSort() {
        explanationArea.append("Starting Insertion Sort...\n");
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;

            currentIndex = i;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;

                nextIndex = j;
                repaint();
                explanationArea.append("Shifting element at index " + (j + 1) + " to the right...\n");
                sleep();
            }
            array[j + 1] = key;
            sorted[i] = true;
            repaint();
        }
        sorted[array.length - 1] = true;
        repaint();
    }

    private void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            split[left] = true;
            split[right] = true;
            repaint();
            explanationArea.append("Splitting array from index " + left + " to " + right + "...\n");
            sleep();

            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);

            split[left] = false;
            split[right] = false;
            merge(array, left, mid, right);
        }

        if (left == 0 && right == array.length - 1) {
            for (int i = left; i <= right; i++) {
                sorted[i] = true;
            }
        }
    }

    private void merge(int[] array, int left, int mid, int right) {
        explanationArea.append("Merging subarrays from index " + left + " to " + right + "...\n");
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, mid + 1, rightArray, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            merge[k] = true;
            repaint();
            explanationArea.append("Comparing elements from left and right subarrays...\n");
            sleep();

            if (leftArray[i] <= rightArray[j]) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            merge[k] = false;
            k++;
        }

        while (i < n1) {
            array[k] = leftArray[i];
            merge[k] = true;
            repaint();
            explanationArea.append("Copying remaining elements from left subarray...\n");
            sleep();
            merge[k] = false;
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = rightArray[j];
            merge[k] = true;
            repaint();
            explanationArea.append("Copying remaining elements from right subarray...\n");
            sleep();
            merge[k] = false;
            j++;
            k++;
        }
    }

    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        explanationArea.append("Partitioning array from index " + low + " to " + high + "...\n");
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            currentIndex = j;
            nextIndex = high;
            repaint();
            explanationArea.append("Comparing element at index " + j + " with pivot...\n");
            sleep();

            if (array[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        sorted[i + 1] = true;
        return i + 1;
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        repaint();
        sleep();
    }

    private void sleep() {
        try {
            while (isPaused) {
                Thread.sleep(300); // Pause interval
            }
            Thread.sleep(speed); // Animation speed
        } catch (InterruptedException ignored) {
        }
    }

    // Method to get time complexity for the selected algorithm
    private String getTimeComplexity() {
        String algorithm = (String) algorithmSelection.getSelectedItem();
        switch (algorithm) {
            case "Bubble Sort":
                return "Time Complexity: Best : O(n) Average : O(n^2) Worst : O(n^2)";
            case "Selection Sort":
                return "Time Complexity: Best : O(n^2) Average : O(n^2) Worst : O(n^2)";
            case "Insertion Sort":
                return "Time Complexity: Best : O(n) Average : O(n^2) Worst : O(n^2)";
            case "Merge Sort":
                return "Time Complexity: Best : O(n log(n)) Average : O(n log(n)) Worst : O(n log(n))";
            case "Quick Sort":
                return "Time Complexity: Best : O(n log(n)) Average : O(n log(n)) Worst : O(n^2)";
            default:
                return "";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (array != null) {
            int width = getWidth() / array.length;
            int height = 50;

            for (int i = 0; i < array.length; i++) {
                if (sorted[i]) {
                    g.setColor(new Color(45, 224, 25)); 
                } else if (merge[i]) {
                    g.setColor(new Color(0, 255, 255)); 
                } else if (split[i]) {
                    g.setColor(new Color(255, 165, 0)); 
                } else if (i == nextIndex) {
                    g.setColor(new Color(187, 115, 51)); 
                } else if (i == currentIndex) {
                    g.setColor(new Color(245, 245, 220)); 
                } else {
                    g.setColor(Color.GRAY); 
                }
                g.fillRect(i * width + 10, getHeight() / 2 - height / 2, width - 20, height);

                // Draw the array values inside the bars
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString(Integer.toString(array[i]), i * width + width / 2 - 10, getHeight() / 2 + 5);

                // Draw arrows above the current comparison elements
                if (arrows[i]) {
                    g.setColor(new Color(242, 83, 15));
                    int arrowX = i * width + width / 2;
                    int[] xPoints = {arrowX - 15, arrowX + 15, arrowX};
                    int[] yPoints = {getHeight() / 2 - height / 2 - 30, getHeight() / 2 - height / 2 - 30, getHeight() / 2 - height / 2 - 40};
                    g.fillPolygon(xPoints, yPoints, 3);
                }
            }

            // Draw the time complexity at the bottom
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String complexityText = getTimeComplexity();
            g.drawString(complexityText, (getWidth() - g.getFontMetrics().stringWidth(complexityText)) / 2, getHeight() - 120);
        }
    }

  
}