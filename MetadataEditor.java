package org.apache.nicolasBecasPI;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata.GpsInfo;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.formats.tiff.*;

public class MetadataEditor {

    /**
     * 
     * @param jpegImageFile image that will be deleted
     * @param dst image that is the final version
     * @throws IOException
     * @throws ImagingException
     */
	public static void changeExifMetadata(final File jpegImageFile, final File dst) throws IOException, ImagingException {
	    try (FileOutputStream fos = new FileOutputStream(dst);
	         OutputStream os = new BufferedOutputStream(fos)) {

	        TiffOutputSet outputSet = null;
	        ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
	        JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

	        if (jpegMetadata != null) {
	            TiffImageMetadata exif = jpegMetadata.getExif();
	            if (exif != null) {
	                outputSet = exif.getOutputSet();
	            }
	        }

	        if (outputSet == null) {
	            outputSet = new TiffOutputSet();
	        }

	        TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

	        int numeroAleatorio = (int) (Math.random() * 81) + 10;
	        String brillo = String.valueOf(numeroAleatorio);

	        String[] marcasCamara = {"Canon", "Nikon", "Sony", "Fujifilm"};
	        int indiceAleatorio = (int) (Math.random() * marcasCamara.length);
	        String marcaSeleccionada = marcasCamara[indiceAleatorio];

	        LocalDate fechaActual = LocalDate.now();
	        int minDay = 1;
	        int maxDay = 365 * 2;
	        int randomDay = ThreadLocalRandom.current().nextInt(minDay, maxDay + 1);
	        LocalDate fechaAleatoria = fechaActual.plusDays(randomDay);
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
	        String fechaFormateada = fechaAleatoria.format(formatter) + " 00:00:00";

	        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_BRIGHTNESS);
	        exifDirectory.add(ExifTagConstants.EXIF_TAG_BRIGHTNESS, brillo);
	        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_CAMERA_OWNER_NAME);
	        exifDirectory.add(ExifTagConstants.EXIF_TAG_CAMERA_OWNER_NAME, marcaSeleccionada);
	        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
	        exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, fechaFormateada);

	        final double[][] cityCoords = {
	            {-0.8791, 41.6543}, // Zaragoza
	            {-74.0060, 40.7128}, // Nueva York
	            {-80.1918, 25.7617}  // Miami
	        };
	        int randomIndex = (int) (Math.random() * 3);
	        final double longitude = cityCoords[randomIndex][0];
	        final double latitude = cityCoords[randomIndex][1];

	        outputSet.setGpsInDegrees(longitude, latitude);

	        new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
	    }
	}

	public static void readImageMeta(String imagePath) throws IOException {
	    File pngImageFile = new File(imagePath);
	    final ImageMetadata metadata = Imaging.getMetadata(pngImageFile);
	    System.out.println(metadata);
	}

	public static List<MetadatosImagen> processImagesInFolder(File folder) {
	    List<MetadatosImagen> metadatosImagenList = new ArrayList<>();

	    if (folder.exists() && folder.isDirectory()) {
	        File[] files = folder.listFiles();
	        for (File file : files) {
	            if (file.isDirectory()) {
	                metadatosImagenList.addAll(processImagesInFolder(file));
	            } else if (file.isFile() && isImageFile(file)) {
	                try {
	                    MetadatosImagen met = extractMetadata(file);
	                    metadatosImagenList.add(met);
	                } catch (IOException e) {
	                    System.err.println("Error al procesar la imagen: " + file.getName());
	                    e.printStackTrace();
	                }
	            }
	        }
	    } else {
	        System.err.println("La ruta especificada no es una carpeta válida.");
	    }
	    return metadatosImagenList;
	}

	private static boolean isImageFile(File file) {
	    String fileName = file.getName().toLowerCase();
	    return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
	}

	public static MetadatosImagen extractMetadata(File jpegImageFile) throws IOException {
	    ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
	    JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
	    TiffImageMetadata exifMetadata = jpegMetadata.getExif();

	    if (exifMetadata != null) {
	        TiffField brightnessField = exifMetadata.findField(ExifTagConstants.EXIF_TAG_BRIGHTNESS);
	        String brillo = brightnessField.getStringValue();

	        TiffField cameraOwnerField = exifMetadata.findField(ExifTagConstants.EXIF_TAG_CAMERA_OWNER_NAME);
	        String camara = cameraOwnerField.getStringValue();

	        TiffField dateTimeOriginalField = exifMetadata.findField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
	        String data = dateTimeOriginalField.getStringValue();

	        GpsInfo gpsInfo = exifMetadata.getGpsInfo();
	        double latitude = gpsInfo.getLatitudeAsDegreesNorth();
	        double longitude = gpsInfo.getLongitudeAsDegreesEast();

	        String name = jpegImageFile.getName();
	        String path = jpegImageFile.getPath();
	        String parent = jpegImageFile.getParent();

	        return new MetadatosImagen(brillo, camara, data, longitude, latitude, name, path, parent);
	    }

	    return null;
	}

    
    public static List<MetadatosImagen> orderByFechaAsc(List<MetadatosImagen> metadatosImagenList) {
        metadatosImagenList.sort(Comparator.comparing(MetadatosImagen::getFechaOriginal));
        return metadatosImagenList;
    }

    public static List<MetadatosImagen> orderByFechaDesc(List<MetadatosImagen> metadatosImagenList) {
        metadatosImagenList.sort(Comparator.comparing(MetadatosImagen::getFechaOriginal).reversed());
        return metadatosImagenList;
    }

    public static List<MetadatosImagen> orderByBrilloAsc(List<MetadatosImagen> metadatosImagenList) {
        metadatosImagenList.sort(Comparator.comparing(MetadatosImagen::getBrillo));
        return metadatosImagenList;
    }

    public static List<MetadatosImagen> orderByBrilloDesc(List<MetadatosImagen> metadatosImagenList) {
        metadatosImagenList.sort(Comparator.comparing(MetadatosImagen::getBrillo).reversed());
        return metadatosImagenList;
    }
    

    public static List<MetadatosImagen> filterByAno(List<MetadatosImagen> metadatosImagenList, String ano) {
        return metadatosImagenList.stream()
                .filter(imagen -> {
                    String fechaOriginal = imagen.getFechaOriginal();
                    String[] partes = fechaOriginal.split(":");
                    return partes[0].equals(ano);
                })
                .collect(Collectors.toList());
    }

    public static List<MetadatosImagen> filterByUbicacion(List<MetadatosImagen> metadatosImagenList, String ubicacion) {
        return metadatosImagenList.stream()
                .filter(imagen -> imagen.getUbicacion().equals(ubicacion))
                .collect(Collectors.toList());
    }

    public static List<MetadatosImagen> filterByMaker(List<MetadatosImagen> metadatosImagenList, String camara) {
        return metadatosImagenList.stream()
                .filter(imagen -> imagen.getMarcaCamara().equals(camara))
                .collect(Collectors.toList());
    }

  

   
}