import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextEditorApp {
    private final TextEditorModel model;
    private final TextEditorView view;
    private final TextEditorController controller;

    public TextEditorApp() {
        model = new TextEditorModel();
        view = new TextEditorView();
        controller = new TextEditorController(model, view);

        view.initUI(controller);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TextEditorApp app = new TextEditorApp();
            app.start();
        });
    }

    public void start() {
        view.showUI();
    }
}

class TextEditorModel {
    private List<String> lines = new ArrayList<>();

    public void addLine(String line) {
        lines.add(line);
    }

    public void removeLine(int index) {
        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
        }
    }

    public List<String> getLines() {
        return lines;
    }

    public void saveToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void loadFromFile(File file) throws IOException {
        lines = Files.lines(Paths.get(file.getAbsolutePath())).collect(Collectors.toList());
    }
}

class TextEditorView {
    private JFrame frame;
    private JTextArea textArea;
    private TextEditorController controller;

    public void initUI(TextEditorController controller) {
        this.controller = controller;
        frame = new JFrame("Text Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        textArea.setEditable(false);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Line");
        JButton removeButton = new JButton("Remove Line");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");

        addButton.addActionListener(e -> controller.addLine());
        removeButton.addActionListener(e -> controller.removeLine());
        saveButton.addActionListener(e -> controller.saveToFile());
        loadButton.addActionListener(e -> controller.loadFromFile());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void showUI() {
        frame.setVisible(true);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void updateTextArea(List<String> lines) {
        textArea.setText(String.join("\n", lines));
    }
}

class TextEditorController {
    private final TextEditorModel model;
    private final TextEditorView view;

    public TextEditorController(TextEditorModel model, TextEditorView view) {
        this.model = model;
        this.view = view;
    }

    public void addLine() {
        String line = JOptionPane.showInputDialog(view.getTextArea(), "Enter text:");
        if (line != null && !line.isEmpty()) {
            model.addLine(line);
            view.updateTextArea(model.getLines());
        }
    }

    public void removeLine() {
        String input = JOptionPane.showInputDialog(view.getTextArea(), "Enter line number to remove:");
        if (input != null && !input.isEmpty()) {
            try {
                int index = Integer.parseInt(input);
                model.removeLine(index);
                view.updateTextArea(model.getLines());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view.getTextArea(), "Invalid input. Please enter a valid number.");
            }
        }
    }

    public void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(view.getTextArea());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                model.saveToFile(file);
                JOptionPane.showMessageDialog(view.getTextArea(), "Text saved to file successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getTextArea(), "Error occurred while saving to file: " + e.getMessage());
            }
        }
    }

    public void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(view.getTextArea());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                model.loadFromFile(file);
                view.updateTextArea(model.getLines());
                JOptionPane.showMessageDialog(view.getTextArea(), "Text loaded from file successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getTextArea(), "Error occurred while loading from file: " + e.getMessage());
            }
        }
    }
}
