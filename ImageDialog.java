package org.apache.nicolasBecasPI;

import javax.swing.*;

public class ImageDialog extends JDialog {
    private static final long serialVersionUID = 1L;
	private JTextField brilloField;
    private JTextField marcaCamaraField;
    private JTextField fechaOriginalField;
    private JTextField ubicacionField;

    
    public ImageDialog(JFrame parent, MetadatosImagen meta) {
        super(parent, "Editar Metadatos", true);
        
        brilloField = new JTextField(meta.getBrillo(), 10);
        marcaCamaraField = new JTextField(meta.getMarcaCamara(), 10);
        fechaOriginalField = new JTextField(meta.getFechaOriginal(), 10);
        ubicacionField = new JTextField(String.valueOf(meta.getUbicacion()), 10);
        
        JLabel brilloLabel = new JLabel("Brillo:");
        JLabel marcaCamaraLabel = new JLabel("Marca de la cámara:");
        JLabel fechaOriginalLabel = new JLabel("Fecha original:");
        JLabel ubicacionLabel = new JLabel("Ubicacion:");
  
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.add(brilloLabel);
        panel.add(brilloField);
        panel.add(marcaCamaraLabel);
        panel.add(marcaCamaraField);
        panel.add(fechaOriginalLabel);
        panel.add(fechaOriginalField);
        panel.add(ubicacionLabel);
        panel.add(ubicacionField);

        
  
        JButton acceptButton = new JButton("Aceptar");
        acceptButton.addActionListener(e -> {
            meta.setBrillo(brilloField.getText());
            meta.setMarcaCamara(marcaCamaraField.getText());
            meta.setFechaOriginal(fechaOriginalField.getText());
            meta.setUbicacion(ubicacionField.getText());

            
  
            dispose();
        });
        panel.add(acceptButton);
        

        getContentPane().add(panel);
        
    
        pack();
        setLocationRelativeTo(parent);
    }
}
