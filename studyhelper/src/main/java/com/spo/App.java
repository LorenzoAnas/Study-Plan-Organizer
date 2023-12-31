package com.spo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.spo.model.Project;

/**
 * The App class is the main class of the Study Plan Organizer application.
 * It creates a GUI that allows the user to manage projects.
 * The GUI displays a list of projects on the left side, and project details on the right side.
 * The user can add, delete, and edit projects.
 * The application stores the projects in a CSV file.
 */
public class App {
    public static class ProjectManager {
        private JFrame mainFrame;
        private JList<String> projectList;
        private DefaultListModel<String> listModel;
        private JLabel daysLeftLabel, tasksLeftLabel, ratioLabel, dateLabel;
        private List<Project> projects;
        private DateTimeFormatter dateFormatter;

        public ProjectManager() {
            mainFrame = new JFrame("Project Manager");
            mainFrame.setSize(600, 400);
            mainFrame.setLayout(new BorderLayout());

            ((JComponent) mainFrame.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            // Initialize Folder List
            listModel = new DefaultListModel<>();
            projectList = new JList<>(listModel); 
            projectList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        showContextMenu(evt);
                    } else if (evt.getClickCount() == 2) {
                        showProjectDetails();
                    }
                }
            });
       

            // Project Details
            JPanel projectDetailsPanel = new JPanel();
            projectDetailsPanel.setLayout(new BorderLayout());

            JPanel labelsPanel = new JPanel();
            labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));

            // Border and Font for Labels
            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
            Font font = new Font("Arial", Font.BOLD, 18);

            // Labels
            daysLeftLabel = new JLabel("Days Left: "); 
            daysLeftLabel.setFont(font);
            daysLeftLabel.setBorder(border);


            tasksLeftLabel = new JLabel("Tasks Left: "); 
            tasksLeftLabel.setFont(font);
            tasksLeftLabel.setBorder(border);

            ratioLabel = new JLabel("Task/Days Ratio: "); 
            ratioLabel.setFont(font);
            ratioLabel.setBorder(border);

            // Add Project Button
            JButton addProjectButton = new JButton("+ New Project"); 
            addProjectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showNewProjectDialog();
                }
            });

            // Add labels to labelsPanel with 10 pixel vertical margin
            labelsPanel.add(daysLeftLabel); 
            labelsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            labelsPanel.add(tasksLeftLabel);
            labelsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            labelsPanel.add(ratioLabel);
            labelsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            labelsPanel.add(addProjectButton);

            projectDetailsPanel.add(labelsPanel, BorderLayout.CENTER);

            // Add projectList and projectDetailsPanel to mainFrame
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(projectList), projectDetailsPanel);
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(100);
            mainFrame.add(splitPane, BorderLayout.CENTER);   

            // Date Display
            JPanel datePanel = new JPanel();
            dateLabel = new JLabel();
            dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateLabel.setText(dateFormatter.format(LocalDate.now()));
            datePanel.add(dateLabel);
            mainFrame.add(datePanel, BorderLayout.NORTH);

            // Load projects from file
            projects = new ArrayList<>();
            loadProjects();
            for (Project project : projects) {
                listModel.addElement(project.getName()); 
            }

            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        }

        private void showContextMenu(MouseEvent evt) {
            JPopupMenu contextMenu = new JPopupMenu();
            JMenuItem addProjectMenuItem = new JMenuItem("Add New Project");
            addProjectMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showNewProjectDialog();
                }
            });
            JMenuItem deleteProjectMenuItem = new JMenuItem("Delete Selected Project");
            deleteProjectMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = projectList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete this project?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            listModel.remove(selectedIndex);
                            projects.remove(selectedIndex);
                            saveProjects();
                        }
                    }
                }
            });

            JMenuItem editProjectMenuItem = new JMenuItem("Edit Selected Project");
            editProjectMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showEditProjectDialog();
                }
            });

            contextMenu.add(editProjectMenuItem);
            contextMenu.add(addProjectMenuItem);
            contextMenu.add(deleteProjectMenuItem);
            contextMenu.show(projectList, evt.getX(), evt.getY());
        }

        private void showNewProjectDialog() {
            JDialog newProjectDialog = new JDialog(mainFrame, "New Project", true);
            newProjectDialog.setSize(300, 200);
            newProjectDialog.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setBounds(10, 10, 80, 25);
            inputPanel.add(nameLabel);
            JTextField nameField = new JTextField();
            nameField.setBounds(100, 10, 160, 25);
            inputPanel.add(nameField);
            JLabel dueDateLabel = new JLabel("Due Date:");
            dueDateLabel.setBounds(10, 40, 80, 25);
            inputPanel.add(dueDateLabel);
            JTextField dueDateField = new JTextField();
            dueDateField.setBounds(100, 40, 160, 25);
            inputPanel.add(dueDateField);
            JLabel tasksLabel = new JLabel("Number of Tasks:");
            tasksLabel.setBounds(10, 70, 120, 25);
            inputPanel.add(tasksLabel);
            JTextField tasksField = new JTextField();
            tasksField.setBounds(130, 70, 130, 25);
            inputPanel.add(tasksField);
            newProjectDialog.add(inputPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton createButton = new JButton("Create");
            createButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameField.getText();
                    String dueDateString = dueDateField.getText();
                    int tasks = Integer.parseInt(tasksField.getText());
                    try {
                        LocalDate dueDate = LocalDate.parse(dueDateString, dateFormatter);
                        Project project = new Project(name, dueDate, tasks);
                        projects.add(project);
                        listModel.addElement(name);
                        saveProjects();
                        newProjectDialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newProjectDialog, "Invalid date format. Please use the format dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE); // fix: add error message, it doesn't show
                    }
                }
            });

            buttonPanel.add(createButton);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newProjectDialog.dispose();
                }
            });

            buttonPanel.add(cancelButton);
            newProjectDialog.add(buttonPanel, BorderLayout.SOUTH);

            newProjectDialog.setVisible(true);
        }

        private void showEditProjectDialog() {
            int selectedIndex = projectList.getSelectedIndex();
            if (selectedIndex != -1) {
                Project project = projects.get(selectedIndex);
                // Open a dialog similar to the "New Project" dialog, but pre-filled with the selected project's details
                JDialog editDialog = new JDialog(mainFrame, "Edit Project", true);
                editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                editDialog.setLayout(new BorderLayout());

                JPanel inputPanel = new JPanel();
                inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
                inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JLabel nameLabel = new JLabel("Name:");
                nameLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(nameLabel);

                JTextField nameField = new JTextField(project.getName());
                nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
                nameField.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(nameField);

                JLabel dueDateLabel = new JLabel("Due Date (yyyy-MM-dd):");
                dueDateLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(dueDateLabel);

                JTextField dueDateField = new JTextField(project.getDueDate().format(dateFormatter));
                dueDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, dueDateField.getPreferredSize().height));
                dueDateField.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(dueDateField);

                JLabel tasksLabel = new JLabel("Tasks:");
                tasksLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(tasksLabel);

                JTextField tasksField = new JTextField(Integer.toString(project.getTasks()));
                tasksField.setMaximumSize(new Dimension(Integer.MAX_VALUE, tasksField.getPreferredSize().height));
                tasksField.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(tasksField);

                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = nameField.getText();
                        try {
                            LocalDate dueDate = LocalDate.parse(dueDateField.getText(), dateFormatter);
                            project.setName(name);
                            project.setDueDate(dueDate);
                            project.setTasks(Integer.parseInt(tasksField.getText()));
                            listModel.set(selectedIndex, name); 
                            saveProjects();
                            editDialog.dispose();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(editDialog, "Invalid date format. Please use the format dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                saveButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                inputPanel.add(saveButton);

                editDialog.add(inputPanel, BorderLayout.CENTER);
                editDialog.pack();
                editDialog.setLocationRelativeTo(mainFrame);
                editDialog.setVisible(true);
            }
        }

        private void showProjectDetails() {
            int selectedIndex = projectList.getSelectedIndex();
            if (selectedIndex != -1) {
                Project project = projects.get(selectedIndex);
                daysLeftLabel.setText("Days Left: " + project.getDaysLeft());
                tasksLeftLabel.setText("Tasks Left: " + project.getTasks());
                double ratio = project.getTaskDayRatio();
                String formattedRatio = String.format("%.1f", ratio);
                ratioLabel.setText("Task/Days Ratio: " + formattedRatio);
            }
        }

        private void loadProjects() {
            try {
                File file = new File("projects.csv");
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        String name = parts[0];
                        LocalDate dueDate = LocalDate.parse(parts[1], dateFormatter);
                        int tasks = Integer.parseInt(parts[2]);
                        Project project = new Project(name, dueDate, tasks);
                        projects.add(project);
                    }
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveProjects() {
            try {
                File file = new File("projects.csv");
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (Project project : projects) {
                    writer.write(project.getName() + "," + dateFormatter.format(project.getDueDate()) + "," + project.getTasks());
                    writer.newLine();
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        
        

public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            new ProjectManager();
        }
    });
}

    }
}

// TODO: add a menu bar with a "New Project" option
// TODO: add a menu bar with a "Delete Project" option
// TODO: add a menu bar with a "Edit Project" option