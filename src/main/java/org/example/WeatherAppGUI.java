package org.example;

import org.json.simple.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI()
    {
        super("Weather seeker");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 650);

        setBackground(Color.getHSBColor(255, 253, 221));

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGuiComponents();



    }

    private void addGuiComponents()
    {

        JTextField timeData = new JTextField();
        timeData.setBounds(0, 25, 200, 100);
        timeData.setFont(new Font("Dialog", Font.PLAIN, 24));
        timeData.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        timeData.setOpaque(true);


        JTextField searchTxtField = new JTextField();
        searchTxtField.setBounds(30, 500, 296, 32);
        searchTxtField.setFont(new Font("Dialog", Font.PLAIN, 24));
        searchTxtField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        searchTxtField.setOpaque(true);


        add(searchTxtField);

        JLabel weatherCondImage = new JLabel(loadImage("src/assets/lib2/main.png"));
        weatherCondImage.setBounds(0, 25, 450, 217);
        add(weatherCondImage);

        JLabel temperatureTxt = new JLabel("10 C");
        temperatureTxt.setBounds(0, 250, 450, 54);
        temperatureTxt.setFont(new Font("Dialog", Font.BOLD, 56));
        temperatureTxt.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureTxt);


        JLabel weatherCondDisc = new JLabel("Weather seeker");
        weatherCondDisc.setBounds(0, 305, 450, 36);
        weatherCondDisc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherCondDisc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherCondDisc);


        JLabel humidityImage = new JLabel(loadImage("src/assets/waterdrop.png"));
        humidityImage.setBounds(15, 400, 74, 66);
        add(humidityImage);

        JLabel humidityTxt = new JLabel("<html><b>Влажность 100%</b></html>");
        humidityTxt.setBounds(90, 400, 100, 55);
        humidityTxt.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityTxt);

        JLabel windSpeedImage = new JLabel(loadImage("src/assets/lib2/wind2.png"));
        windSpeedImage.setBounds(220, 400, 74, 66);
        add(windSpeedImage);

        JLabel windSpeedTxt = new JLabel("<html><b>Ветер<br>15 км/ч</b></html>");
        windSpeedTxt.setBounds(310, 400, 100, 55);
        windSpeedTxt.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedTxt);

        JButton searchButton = new JButton();
        searchButton.setText("Найти");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(330, 500, 82, 32);
        searchButton.setBackground(Color.getHSBColor(255, 253, 221));
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTxtField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0)
                {
                    return;
                }

                weatherData = WeatherAppLogic.getWeatherData(userInput);

                String weatherCond = (String) weatherData.get("weather_condition");

                switch (weatherCond) {
                    case "Ясно" -> weatherCondImage.setIcon(loadImage("src/assets/lib2/sunnymain.png"));
                    case "Дождливо" -> weatherCondImage.setIcon(loadImage("src/assets/lib2/rainy.png"));
                    case "Облачно" -> weatherCondImage.setIcon(loadImage("src/assets/lib2/cloudymain.png"));
                    case "Снег" -> weatherCondImage.setIcon(loadImage("src/assets/lib2/snowy.png"));
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureTxt.setText(temperature + " C");

                weatherCondDisc.setText(weatherCond);

                long humidity = (long) weatherData.get("humidity");
                humidityTxt.setText("<html><b>Влажность " + humidity + "%</b></html>");

                double windspeed = (double) weatherData.get("windspeed");
                windSpeedTxt.setText("<html><b>Ветер<br>" + windspeed + " км/ч</b></html>");
            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String s) {
        try {
            BufferedImage image = ImageIO.read(new File(s));
            return new ImageIcon(image);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("No such element");

        return null;
    }
}
