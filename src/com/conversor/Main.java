package com.conversor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            // Aquí montamos el menú para que el usuario elija qué hacer
            System.out.println("=== Conversor de Monedas ===");
            System.out.println("Seleccione una opción:");
            System.out.println("1. Convertir de Dólares a Pesos Colombianos");
            System.out.println("2. Convertir de Pesos Colombianos a Dólares");
            System.out.println("3. Convertir de Dólares a Euros");
            System.out.println("4. Convertir de Euros a Dólares");
            System.out.println("5. Convertir de Euros a Pesos Colombianos");
            System.out.println("6. Convertir de Pesos Colombianos a Euros");
            System.out.println("7. Salir");
            System.out.print("Ingrese su opción: ");
            opcion = scanner.nextInt();

            // Dependiendo de la opción seleccionada, hacemos algo
            switch (opcion) {
                case 1:
                    convertirMoneda("USD", "COP");
                    break;
                case 2:
                    convertirMoneda("COP", "USD");
                    break;
                case 3:
                    convertirMoneda("USD", "EUR");
                    break;
                case 4:
                    convertirMoneda("EUR", "USD");
                    break;
                case 5:
                    convertirMoneda("EUR", "COP");
                    break;
                case 6:
                    convertirMoneda("COP", "EUR");
                    break;
                case 7:
                    System.out.println("¡Gracias por usar el conversor de monedas!"); // Esto cierra el programa
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, intente de nuevo."); // Por si eligen algo raro
            }
        } while (opcion != 7); // Esto hace que el menú siga saliendo hasta que seleccionen "7"
        scanner.close();
    }

    public static void convertirMoneda(String monedaOrigen, String monedaDestino) {
        try {
            // Esta es la clave de la API que generaste, asegúrate de que sea correcta
            String apiKey = "8be79004d81d5acaa585b21b";
            String baseUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + monedaOrigen;

            // Conectamos a la API
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Si algo falla aquí, el código lo detecta con el responseCode
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) { // 200 significa "todo bien"
                throw new RuntimeException("Error HTTP: " + responseCode);
            }

            // Aquí leemos la respuesta que nos manda la API
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Usamos Gson para convertir el JSON de la API en algo entendible
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

            // Sacamos las tasas de cambio
            JsonObject rates = jsonResponse.getAsJsonObject("conversion_rates");
            double tasa = rates.get(monedaDestino).getAsDouble();

            // Pedimos la cantidad que quieren convertir
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese la cantidad en " + monedaOrigen + ": ");
            double cantidad = scanner.nextDouble();

            // Calculamos el resultado y lo mostramos
            double resultado = cantidad * tasa;
            System.out.println("El equivalente de " + cantidad + " " + monedaOrigen + " es " + resultado + " " + monedaDestino);

        } catch (Exception e) {
            // Por si algo explota, mostramos un mensaje y el error técnico
            System.out.println("Ocurrió un error al conectar con la API o realizar la conversión.");
            e.printStackTrace();
        }
    }
}