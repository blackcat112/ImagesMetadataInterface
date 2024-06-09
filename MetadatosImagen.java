package org.apache.nicolasBecasPI;


public class MetadatosImagen {
    private String brillo;
    private String marcaCamara;
    private String fechaOriginal;
    private String Ubicacion;
    private double longitud;
    private double latitud;
    private String name;
    private String path;
    private String padre;


    public MetadatosImagen(String brillo, String marcaCamara, String fechaOriginal, double longitud, double latitud,String name,String path,String carpeta) {
        this.brillo = this.adjustarBrillo(brillo);
        this.marcaCamara = marcaCamara;
        this.fechaOriginal = fechaOriginal;
        this.longitud = longitud;
        this.latitud = latitud;
        this.Ubicacion = this.createUbicacion(this.latitud,this.longitud);
        this.name = name;
        this.path = path;
        this.padre = carpeta;
    }

    // Getters y Setters
    public String adjustarBrillo(String brillo) {
        int brightness = Integer.parseInt(brillo);
        int roundedBrightness = (int) Math.round(brightness / 10.0) * 10;
        return String.valueOf(roundedBrightness);
    }

    public String getBrillo() {
       return this.brillo;
    }
    
    public void setBrillo(String brillo) {
        this.brillo = brillo;
    }

    public String getMarcaCamara() {
        return marcaCamara;
    }

    public void setMarcaCamara(String marcaCamara) {
        this.marcaCamara = marcaCamara;
    }

    public String getFechaOriginal() {
        return fechaOriginal;
    }

    public void setFechaOriginal(String fechaOriginal) {
        this.fechaOriginal = fechaOriginal;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    // Método para imprimir los metadatos
    public void imprimirMetadatos() {
        System.out.println("Metadatos de la imagen: " + this.name);
        System.out.println("Brillo: " + brillo);
        System.out.println("Marca de la cámara: " + marcaCamara);
        System.out.println("Fecha original: " + fechaOriginal);
        System.out.println("Ubicacion: " + Ubicacion);
        System.out.println("--------------------------------------");
    }

    public String createUbicacion(double latitud, double longitud) {
        final double[][] cityCoords = {
            {-0.8791, 41.6543}, // Zaragoza
            {-74.0060, 40.7128}, // Nueva York
            {-80.1918, 25.7617} // Miami
        };

        String ubicacion = "";

        for (int i = 0; i < cityCoords.length; i++) {
            double cityLat = cityCoords[i][1];
            double cityLong = cityCoords[i][0];

            if (latitud == cityLat && longitud == cityLong) {
                switch (i) {
                    case 0:
                        ubicacion = "Zaragoza";
                        break;
                    case 1:
                        ubicacion = "Nueva York";
                        break;
                    case 2:
                        ubicacion = "Miami";
                        break;
                    default:
                        ubicacion = "Desconocida";
                }
            }
        }
        
        return ubicacion;
    }

	public String getUbicacion() {
		return Ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		Ubicacion = ubicacion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPadre() {
		return padre;
	}

	public void setPadre(String padre) {
		this.padre = padre;
	}
	
	


}