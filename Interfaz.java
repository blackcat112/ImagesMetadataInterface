package org.apache.nicolasBecasPI;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Interfaz extends JFrame {
    private static final long serialVersionUID = 1L;
    private static boolean alternar = false;

    Container base = new Container();
    Container carpetas = new Container();
    Container info = new Container();

 
    List<MetadatosImagen> metadatosImagenList;
    List<MetadatosImagen> metadatosImagenList2; // show filter list
    List<MetadatosImagen> copia; // filter's list 

    public Interfaz() {
        String directorioActual = System.getProperty("user.dir"); // get user directory
        String rutaRaiz = directorioActual + File.separator + "Album";

        DirectoryGenerator.generateDirectoryStructureWithImages(2, 2, directorioActual,15);

        File folder = new File(rutaRaiz);
        metadatosImagenList = MetadataEditor.processImagesInFolder(folder);
        copia = metadatosImagenList;
        
        //Create the tree 
        
        JTree tree = new JTree(createTreeModel(folder));
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null) return;

                String selectedPath = getPathFromTreeNode(selectedNode);
                File selectedFile = new File(selectedPath);

                if (esDirectorio(selectedPath)) {
                    mostrarTablaMetadatos(selectedPath, metadatosImagenList);
                } else if (esImagen(selectedFile)) {
                    mostrarImagen(selectedPath);
                    mostrarMetadatos(selectedFile.getName());
                }
            }
        });

        base.setLayout(new BorderLayout());
        carpetas.setLayout(new GridLayout(1, 1));
        info.setLayout(new GridLayout(1, 2));

        this.setTitle("AlbumFotos");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        base = this.getContentPane();

        JScrollPane scrollPane = new JScrollPane(tree);
        carpetas.add(scrollPane);

        base.add(carpetas, BorderLayout.WEST);
        base.add(info, BorderLayout.CENTER);

        //Create menuBar
        
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Opciones");

        JMenuItem generarImagen = new JMenuItem("Generar Imagen");
        JMenuItem filtrar = new JMenuItem("Filtrar");
        JMenuItem metcha = new JMenuItem("Cambiar metadatos");
        JMenuItem actualizar = new JMenuItem("Actualizar");

        menu.add(generarImagen);
        menu.add(filtrar);
        menu.add(metcha);
        menu.add(actualizar);

        menuBar.add(menu);

        this.setJMenuBar(menuBar);

        metcha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                fileChooser.setCurrentDirectory(new File(rutaRaiz));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Seleccionar Imagen");
                fileChooser.setApproveButtonText("Aceptar");

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedImage = fileChooser.getSelectedFile();
                    for (MetadatosImagen meta : metadatosImagenList) {
                        if (meta.getName().equals(selectedImage.getName())) {
                            ImageDialog dialog = new ImageDialog(null, meta);
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });
        
        actualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copia = metadatosImagenList;

                JTree tree = new JTree(createTreeModel(folder));
                tree.addTreeSelectionListener(new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent e) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                        if (selectedNode == null) return;

                        String selectedPath = getPathFromTreeNode(selectedNode);
                        File selectedFile = new File(selectedPath);

                        if (esDirectorio(selectedPath)) {
                            mostrarTablaMetadatos(selectedPath, metadatosImagenList);
                        } else if (esImagen(selectedFile)) {
                            mostrarImagen(selectedPath);
                            mostrarMetadatos(selectedFile.getName());
                        }
                    }
                });
                JScrollPane scrollPane = new JScrollPane(tree);
                carpetas.removeAll();
                carpetas.add(scrollPane);
                carpetas.revalidate();
                carpetas.repaint();
            }
        });

        generarImagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(rutaRaiz));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setDialogTitle("Seleccionar Directorio");
                fileChooser.setApproveButtonText("Aceptar");

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    String selectedPath = selectedDirectory.getAbsolutePath();
                    JTextField filenameField = new JTextField();
                    Object[] message = {
                        "Ingrese el nombre del archivo (termina en .jpg):", filenameField
                    };
                    int option = JOptionPane.showConfirmDialog(null, message, "Guardar como", JOptionPane.OK_CANCEL_OPTION);

                    if (option == JOptionPane.OK_OPTION) {
                        String filename = filenameField.getText();
                        if (filename.endsWith(".jpg")) {
                            Random rand = new Random();
                            int width = rand.nextInt(1901) + 100;
                            int height = rand.nextInt(1401) + 100;

                            if (width > height * 2) {
                                width = height * 2;
                            } else if (height > width * 2) {
                                height = width * 2;
                            }
                            int randomColours = rand.nextInt(10) + 1;
                            int randPrimitives = rand.nextInt(4) + 3;

                            String imagePath = selectedPath + File.separator + filename + "_";
                            String imagePath2 = selectedPath + File.separator + filename;
                            try {
                                ImageGenerator.generateImage(width, height, randPrimitives, randomColours, imagePath);
                                ImageGenerator.generateImage(width, height, randPrimitives, randomColours, imagePath2);

                                File imageFile = new File(imagePath);
                                File imageFile2 = new File(imagePath2);
                                MetadataEditor.changeExifMetadata(imageFile, imageFile2);
                                metadatosImagenList = MetadataEditor.processImagesInFolder(folder);
                                if (imageFile.exists() && !imageFile.delete()) {
                                    System.err.println("No se pudo borrar la imagen.");
                                }
                            } catch (IOException t) {
                                t.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "El nombre del archivo debe terminar en .jpg", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        filtrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog filterDialog = new JDialog();
                filterDialog.setSize(300, 200);

                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());

                JLabel label = new JLabel("Filtrar por:");
                panel.add(label, BorderLayout.NORTH);

                String[] options = {"Ciudades", "Año", "Cámara"};
                JComboBox<String> comboBox = new JComboBox<>(options);
                panel.add(comboBox, BorderLayout.CENTER);

                JPanel checkboxPanel = new JPanel();
                checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
                panel.add(checkboxPanel, BorderLayout.SOUTH);

                comboBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkboxPanel.removeAll();

                        String selectedItem = (String) comboBox.getSelectedItem();
                        if ("Ciudades".equals(selectedItem)) {
                            JCheckBox madrid = new JCheckBox("Miami");
                            JCheckBox barcelona = new JCheckBox("Nueva York");
                            JCheckBox valencia = new JCheckBox("Zaragoza");
                            checkboxPanel.add(madrid);
                            checkboxPanel.add(barcelona);
                            checkboxPanel.add(valencia);
                        } else if ("Año".equals(selectedItem)) {
                            JCheckBox _2021 = new JCheckBox("2024");
                            JCheckBox _2022 = new JCheckBox("2025");
                            JCheckBox _2023 = new JCheckBox("2026");
                            checkboxPanel.add(_2021);
                            checkboxPanel.add(_2022);
                            checkboxPanel.add(_2023);
                        } else if ("Cámara".equals(selectedItem)) {
                            JCheckBox nikon = new JCheckBox("Nikon");
                            JCheckBox canon = new JCheckBox("Canon");
                            JCheckBox sony = new JCheckBox("Sony");
                            JCheckBox fuji = new JCheckBox("Fujifilm");
                            checkboxPanel.add(nikon);
                            checkboxPanel.add(canon);
                            checkboxPanel.add(sony);
                            checkboxPanel.add(fuji);
                        }

                        JButton aceptarButton = new JButton("Aceptar");
                        checkboxPanel.add(aceptarButton);

                        aceptarButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String selecionado = "";
                                for (Component component : checkboxPanel.getComponents()) {
                                    if (component instanceof JCheckBox) {
                                        JCheckBox checkBox = (JCheckBox) component;
                                        if (checkBox.isSelected()) {
                                            selecionado = checkBox.getText();
                                            break;
                                        }
                                    }
                                }

                                switch (selectedItem) {
                                    case "Ciudades":
                                        metadatosImagenList2 = MetadataEditor.filterByUbicacion(copia, selecionado);
                                        mostrarTablaMetadatosF(metadatosImagenList2);
                                        copia = metadatosImagenList2;
                                        filterDialog.dispose();
                                        break;
                                    case "Año":
                                        metadatosImagenList2 = MetadataEditor.filterByAno(copia, selecionado);
                                        mostrarTablaMetadatosF(metadatosImagenList2);
                                        copia = metadatosImagenList2;
                                        filterDialog.dispose();
                                        break;
                                    case "Cámara":
                                        metadatosImagenList2 = MetadataEditor.filterByMaker(copia, selecionado);
                                        mostrarTablaMetadatosF(metadatosImagenList2);
                                        copia = metadatosImagenList2;
                                        filterDialog.dispose();
                                        break;
                                }
                            }
                        });

                        filterDialog.revalidate();
                        filterDialog.repaint();
                    }
                });

                filterDialog.add(panel);
                filterDialog.setVisible(true);
            }
        });

  
    

        this.setVisible(true);
    }

    private DefaultTreeModel createTreeModel(File folder) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(folder.getName());
        addNodes(root, folder);
        return new DefaultTreeModel(root);
    }

    private void addNodes(DefaultMutableTreeNode currentNode, File currentFile) {
        if (currentFile.isDirectory()) {
            File[] files = currentFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
                    currentNode.add(childNode);
                    addNodes(childNode, file);
                }
            }
        }
    }

    private String getPathFromTreeNode(DefaultMutableTreeNode node) {
        StringBuilder path = new StringBuilder();
        Object[] nodes = node.getUserObjectPath();
        for (Object n : nodes) {
            path.append(n.toString()).append(File.separator);
        }
        String finalPath = path.toString();
        if (finalPath.endsWith(File.separator)) {
            finalPath = finalPath.substring(0, finalPath.length() - 1);
        }
        return finalPath;
    }

    private boolean esImagen(File file) {
        String imageExtensions = "jpg";
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(imageExtensions)) {
                return true;
            }
      return false;
    }

    private boolean esDirectorio(String path) {
        String directorioActual = System.getProperty("user.dir");
        String rutaCompleta = directorioActual + File.separator + path;
        File f = new File(rutaCompleta);
        return f.isDirectory();
    }

    private void mostrarImagen(String rutaImagen) {
        String directorioActual = System.getProperty("user.dir");
        String rutaCompleta = directorioActual + File.separator + rutaImagen;

        ImageIcon imagenIcon = new ImageIcon(rutaCompleta);
        JLabel labelImagen = new JLabel(imagenIcon);

        int anchoImagen = imagenIcon.getIconWidth();
        int altoImagen = imagenIcon.getIconHeight();
        int anchoContenedor = info.getWidth();
        int altoContenedor = info.getHeight();

        double factorEscala;
        if (anchoImagen > anchoContenedor || altoImagen > altoContenedor) {
            factorEscala = Math.min((double) anchoContenedor / anchoImagen, (double) altoContenedor / altoImagen);
        } else {
            factorEscala = 1.0;
        }

        int anchoEscalado = (int) (anchoImagen * factorEscala);
        int altoEscalado = (int) (altoImagen * factorEscala);

        Image imagenEscalada = imagenIcon.getImage().getScaledInstance(anchoEscalado, altoEscalado, Image.SCALE_SMOOTH);
        ImageIcon imagenEscaladaIcon = new ImageIcon(imagenEscalada);
        labelImagen.setIcon(imagenEscaladaIcon);

        info.removeAll();
        info.add(labelImagen);
        info.revalidate();
        info.repaint();
    }

    private void mostrarMetadatos(String nombreArchivo) {
        MetadatosImagen metadatosImagen = null;

        for (MetadatosImagen metadata : metadatosImagenList) {
            if (metadata.getName().equals(nombreArchivo)) {
                metadatosImagen = metadata;
                break;
            }
        }

        if (metadatosImagen != null) {
            DefaultTableModel modeloTabla = new DefaultTableModel();
            modeloTabla.addColumn("Nombre");
            modeloTabla.addColumn("Camara");
            modeloTabla.addColumn("Ubicacion");
            modeloTabla.addColumn("Brillo");
            modeloTabla.addColumn("Fecha");

            Object[] fila = {nombreArchivo, metadatosImagen.getMarcaCamara(), metadatosImagen.getUbicacion(), metadatosImagen.getBrillo(), metadatosImagen.getFechaOriginal()};
            modeloTabla.addRow(fila);

            JTable tablaMetadatos = new JTable(modeloTabla);
            tablaMetadatos.setFillsViewportHeight(true);

            info.add(new JScrollPane(tablaMetadatos));
            info.revalidate();
            info.repaint();
        }
    }

    private void mostrarTablaMetadatos(String rutaCarpeta, List<MetadatosImagen> metadatosImagenList) {
        String directorioActual = System.getProperty("user.dir");
        String rutaCompleta = directorioActual + File.separator + rutaCarpeta;
        String rutaRaiz = directorioActual + File.separator + "Album";

        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Ruta");
        modeloTabla.addColumn("Camara");
        modeloTabla.addColumn("Ubicacion");
        modeloTabla.addColumn("Brillo");
        modeloTabla.addColumn("Fecha");

        if (rutaRaiz.equals(rutaCompleta)) {
            for (MetadatosImagen metadata : metadatosImagenList) {
                Object[] fila = {metadata.getName(), metadata.getPath(), metadata.getMarcaCamara(), metadata.getUbicacion(), metadata.getBrillo(), metadata.getFechaOriginal()};
                modeloTabla.addRow(fila);
            }
        } else {
            for (MetadatosImagen metadata : metadatosImagenList) {
                if (metadata.getPadre().equals(rutaCompleta)) {
                    Object[] fila = {metadata.getName(), metadata.getPath(), metadata.getMarcaCamara(), metadata.getUbicacion(), metadata.getBrillo(), metadata.getFechaOriginal()};
                    modeloTabla.addRow(fila);
                }
            }
        }

        JTable tablaMetadatos = new JTable(modeloTabla);
        tablaMetadatos.setFillsViewportHeight(true);

        info.removeAll();
        info.add(new JScrollPane(tablaMetadatos));
        info.revalidate();
        info.repaint();

        JTableHeader header = tablaMetadatos.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int columna = header.columnAtPoint(e.getPoint());
                String nombreColumna = modeloTabla.getColumnName(columna);
                if (nombreColumna.equals("Brillo")) {
                    if (alternar) {
                        funcionCuandoClicEnBrillo1(metadatosImagenList);
                        mostrarTablaMetadatos(rutaCarpeta, metadatosImagenList);
                    } else {
                        funcionCuandoClicEnBrillo2(metadatosImagenList);
                        mostrarTablaMetadatos(rutaCarpeta, metadatosImagenList);
                    }
                    alternar = !alternar;
                }
                if (nombreColumna.equals("Fecha")) {
                    if (alternar) {
                        funcionCuandoClicEnFecha1(metadatosImagenList);
                        mostrarTablaMetadatos(rutaCarpeta, metadatosImagenList);
                    } else {
                        funcionCuandoClicEnFecha2(metadatosImagenList);
                        mostrarTablaMetadatos(rutaCarpeta, metadatosImagenList);
                    }
                    alternar = !alternar;
                }
            }
        });
    }
    
    private void mostrarTablaMetadatosF(List<MetadatosImagen> metadatosImagenList) {
        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Ruta");
        modeloTabla.addColumn("Camara");
        modeloTabla.addColumn("Ubicacion");
        modeloTabla.addColumn("Brillo");
        modeloTabla.addColumn("Fecha");

        for (MetadatosImagen metadata : metadatosImagenList) {
            Object[] fila = {metadata.getName(), metadata.getPath(), metadata.getMarcaCamara(), metadata.getUbicacion(), metadata.getBrillo(), metadata.getFechaOriginal()};
            modeloTabla.addRow(fila);
        }

        JTable tablaMetadatos = new JTable(modeloTabla);
        tablaMetadatos.setFillsViewportHeight(true);

        info.removeAll();
        info.add(new JScrollPane(tablaMetadatos));
        info.revalidate();
        info.repaint();

        JTableHeader header = tablaMetadatos.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int columna = header.columnAtPoint(e.getPoint());
                String nombreColumna = modeloTabla.getColumnName(columna);
                if (nombreColumna.equals("Brillo")) {
                    if (alternar) {
                        funcionCuandoClicEnBrillo1(metadatosImagenList);
                        mostrarTablaMetadatosF(metadatosImagenList);
                    } else {
                        funcionCuandoClicEnBrillo2(metadatosImagenList);
                        mostrarTablaMetadatosF(metadatosImagenList);
                    }
                    alternar = !alternar;
                }
                if (nombreColumna.equals("Fecha")) {
                    if (alternar) {
                        funcionCuandoClicEnFecha1(metadatosImagenList);
                        mostrarTablaMetadatosF(metadatosImagenList);
                    } else {
                        funcionCuandoClicEnFecha2(metadatosImagenList);
                        mostrarTablaMetadatosF(metadatosImagenList);
                    }
                    alternar = !alternar;
                }
            }
        });
    }
    
    private static List<MetadatosImagen>  funcionCuandoClicEnBrillo1(List<MetadatosImagen> metadatosImagenList) {
       return metadatosImagenList =  MetadataEditor.orderByBrilloAsc(metadatosImagenList);
    }
    private static List<MetadatosImagen>  funcionCuandoClicEnBrillo2(List<MetadatosImagen> metadatosImagenList) {
        return metadatosImagenList =  MetadataEditor.orderByBrilloDesc(metadatosImagenList);
     }
    
    private static List<MetadatosImagen>  funcionCuandoClicEnFecha1(List<MetadatosImagen> metadatosImagenList) {
        return metadatosImagenList =  MetadataEditor.orderByFechaAsc(metadatosImagenList);
     }
     private static List<MetadatosImagen>  funcionCuandoClicEnFecha2(List<MetadatosImagen> metadatosImagenList) {
         return metadatosImagenList =  MetadataEditor.orderByFechaDesc(metadatosImagenList);
      }
     public List<MetadatosImagen>  getMetadatosImagenList(){
    	 return metadatosImagenList;
     }
}




