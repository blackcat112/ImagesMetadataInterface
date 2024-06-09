package org.apache.nicolasBecasPI;

import java.io.File;

import java.io.IOException;
import java.util.Random;




public class DirectoryGenerator {
	
	
	
    /**
     * 
     * @param maxLevels max levels of folders in the structure
     * @param numFolders number of folders per level
     * @param rootFolder path of root folder
     * @param numImagesPerFolder number of images per folder that you want
     */
    public static void generateDirectoryStructureWithImages(int maxLevels, int numFolders, String rootFolder, int numImagesPerFolder) {
        File root = new File(rootFolder, "Album");
        if (!root.exists()) {
            root.mkdir();
        }
        generateDirectoriesWithImages(root, maxLevels, numFolders, 0, numImagesPerFolder);
    }

    private static void generateDirectoriesWithImages(File parent, int maxLevels, int numFolders, int level, int numImagesPerFolder) {
        if (level >= maxLevels) {
            return;
        }

        String[] folderNames1 = new String[]{"Verano", "Navidad", "SemanaSanta"};
        String[] folderNames2 = new String[]{"Comida", "Familia"};

        for (int i = 0; i < numFolders; i++) {
            String folderName = (level == 0) ? folderNames1[i % folderNames1.length] : folderNames2[(level - 1) % folderNames2.length];

            File folder = new File(parent, folderName + "_" + i);
            if (!folder.exists()) {
                folder.mkdir();
            }

            if (level == 1) {
                for (String subFolderName : folderNames2) {
                    File subFolder = new File(folder, subFolderName);
                    if (!subFolder.exists()) {
                        subFolder.mkdir();
                    }
                } 
            }

            for (int j = 0; j < numImagesPerFolder; j++) {
                Random rand = new Random();
                int width = rand.nextInt(3000) + 100;
                int height = rand.nextInt(3000) + 100;

                if (width > height * 2) {
                    width = height * 2;
                } else if (height > width * 2) {
                    height = width * 2;
                }
                int randomColours = rand.nextInt(10) + 1;
                int randPrimitives = rand.nextInt(4) + 3;
                int randPhoto = rand.nextInt(800);

                String imagePath = folder.getAbsolutePath() + "/image_copy" + j + ".jpg";
                String imagePath2 = folder.getAbsolutePath() + "/image" + randPhoto + ".jpg";

                try {
                    ImageGenerator.generateImage(width,height, randPrimitives, randomColours, imagePath);
                    ImageGenerator.generateImage(width, height, randPrimitives, randomColours, imagePath2);

                    File imageFile = new File(imagePath);
                    File imageFile2 = new File(imagePath2);
                    MetadataEditor.changeExifMetadata(imageFile, imageFile2);

                    if (imageFile.exists() && !imageFile.delete()) {
                        System.err.println("No se pudo borrar la imagen.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            generateDirectoriesWithImages(folder, maxLevels, numFolders, level + 1, numImagesPerFolder);
        }
    }
}