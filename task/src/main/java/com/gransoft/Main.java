package com.gransoft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

    private static final Dimension BUTTON_SIZE = new Dimension(100, 40);

    private static JTextField textField;

    private static JButton enterButton;
    private static JButton sortButton;

    private static JPanel numberPanel;

    private static JFrame frame;

    private static List<Integer> numbers = new ArrayList<>();

    private static final Random RANDOM = new Random();

    private static boolean isDescending = false;

    public static void main(String[] args) {
        frame = new JFrame("Intro screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 500));
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel questionsNumber = new JLabel("How many numbers to display?");
        questionsNumber.setFont(new Font("Arial", Font.PLAIN, 24));

        panel.add(questionsNumber, gbc);
        gbc.gridy = 1;
        textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(200, 30));
        panel.add(textField, gbc);

        gbc.gridy = 2;
        enterButton = createButtons("Enter", BUTTON_SIZE, Color.blue, Color.white);
        panel.add(enterButton, gbc);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Integer.parseInt(textField.getText()) < 1) {
                        JOptionPane.showMessageDialog(frame, "Please enter a positive number.");
                    } else {
                        showNextScreen(frame, Integer.parseInt(textField.getText()));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
                }
            }
        });
    }

    private static void showNextScreen(JFrame frame, int count) {

        frame.getContentPane().removeAll();
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new BorderLayout());
        numberPanel = new JPanel();
        numberPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        int buttonCount = 0;
        int column = 0;
        int row = 0;
        numbers.clear();
        boolean isPresent = false;
        for (int i = 0; i < count; i++) {
            int genNumber = RANDOM.nextInt(1000) + 1;
            if (genNumber <= 30) {
                isPresent = false;
            }
            numbers.add(genNumber);
        }
        if (!isPresent) {
            int genCount = RANDOM.nextInt(count) + 1;
            for (int i = 0; i < genCount; i++) {
                int genNumber = RANDOM.nextInt(30) + 1;
                int index = RANDOM.nextInt(count);
                numbers.set(index, genNumber);
            }
        }

        for (int i = 0; i < numbers.size(); i++) {
            gbc.gridx = column;
            gbc.gridy = row;

            JButton numberButton = createButtons(String.valueOf(numbers.get(i)), BUTTON_SIZE, Color.blue, Color.white);
            numberPanel.add(numberButton, gbc);

            buttonCount++;

            if (buttonCount % 10 == 0) {
                column++;
                row = 0;
            } else {
                row++;
            }
            numberButton.addActionListener(buttonClick);
        }

        JScrollPane scrollPane = new JScrollPane(numberPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        secondPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        sortButton = createButtons("Sort", BUTTON_SIZE, Color.green, Color.white);
        JButton resetButton = createButtons("Reset", BUTTON_SIZE, Color.green, Color.white);

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(sortButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(resetButton, gbc);

        secondPanel.add(controlPanel, BorderLayout.EAST);

        frame.add(secondPanel);

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SortingThread().start();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                JPanel introPanel = new JPanel(new GridBagLayout());
                JLabel introLabel = new JLabel("How many numbers to display?");
                introLabel.setFont(new Font("Arial", Font.PLAIN, 24));
                gbc.gridx = 0;
                gbc.gridy = 0;
                introPanel.add(introLabel, gbc);
                gbc.gridy = 1;
                introPanel.add(textField, gbc);
                gbc.gridy = 2;
                introPanel.add(enterButton, gbc);
                frame.add(introPanel);
                frame.revalidate();
                frame.repaint();
            }
        });

        frame.revalidate();
        frame.repaint();
    }

    public static JButton createButtons(String text, Dimension dimension, Color color, Color foreground) {
        JButton button = new JButton(text);
        button.setPreferredSize(dimension);
        button.setForeground(foreground);
        button.setBackground(color);
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        return button;
    }

    public static void quickSort(List<Integer> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            SwingUtilities.invokeLater(() -> {
                updateNumberPanel(numberPanel, list);
            });

            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private static ActionListener buttonClick = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println();
            try {
                Integer newCount = Integer.parseInt(((JButton) e.getSource()).getText());

                if (newCount > 30) {
                    JOptionPane.showMessageDialog(frame, "Please select a value smaller or equal to 30.");
                    return;
                }
                textField.setText(String.valueOf(newCount));
                showNextScreen(frame, newCount);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private static int partition(List<Integer> list, int low, int high) {
        int pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (!isDescending) {
                if (list.get(j) < pivot) {
                    i++;
                    Collections.swap(list, i, j);

                    updateNumberPanel(numberPanel, list);
                }
            } else {
                if (list.get(j) > pivot) {
                    i++;
                    Collections.swap(list, i, j);
                    updateNumberPanel(numberPanel, list);
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Collections.swap(list, i + 1, high);

        SwingUtilities.invokeLater(() -> {
            updateNumberPanel(numberPanel, list);
        });

        return i + 1;
    }

    private static void updateNumberPanel(JPanel numberPanel, List<Integer> numbers) {
        numberPanel.removeAll();

        int buttonCount = 0;
        int column = 0;
        int row = 0;

        for (int i = 0; i < numbers.size(); i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = column;
            gbc.gridy = row;
            gbc.insets = new Insets(10, 10, 10, 10);

            JButton numberButton = createButtons(String.valueOf(numbers.get(i)), BUTTON_SIZE, Color.blue, Color.white);
            numberButton.addActionListener(buttonClick);
            numberPanel.add(numberButton, gbc);

            buttonCount++;

            if (buttonCount % 10 == 0) {
                column++;
                row = 0;
            } else {
                row++;
            }
        }
        numberPanel.revalidate();
        numberPanel.repaint();
    }

    static class SortingThread extends Thread {
        public void run() {
            if (isDescending) {
                quickSort(numbers, 0, numbers.size() - 1);
            } else {
                quickSort(numbers, 0, numbers.size() - 1);
            }
            isDescending = !isDescending;
        }
    }
}
