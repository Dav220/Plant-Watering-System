package plant_project;
import org.firmata4j.*;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.IODevice;
import org.firmata4j.I2CDevice;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
public class Main {
    static final int A0 = 14; // Potentiometer
    static final int A1 = 15; // Moisture Sensor
    static final int A2 = 16; // Sound
    static final int D2 = 2; // pump
    static final int D3 = 3; // temperature
    static final int D6 = 6; // Button
    static final int D4 = 4; // red LED
    static final int D13 = 13; // default LED on arduino
    static final byte I2C0 = 0x3C; // OLED Display

    public static void main(String[] args) throws
            IOException, InterruptedException {

        // Define your USB Connection
        String myUSB = "/dev/cu.usbserial-0001";
        // Create a FirmataDevice object with a USB connection.
        IODevice arduino = new FirmataDevice(myUSB);
        // Start up the FirmataDevice object.
        arduino.start();
        arduino.ensureInitializationIsDone();
        // Create an I2C communication object b/w the Arduino chip and the OLED over I2C wires
        // 0x3C is the standard address over these wires for the SSD1306 (or SSD1515)
        // Then create an SSD1306 object using the I2C object with the right pixel size for the OLED
        I2CDevice i2cObject = arduino.getI2CDevice((byte) 0x3C); // Use 0x3C for the Grove OLED
        SSD1306 Oled = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515
        // Initialize the OLED (SSD1306) object
        Oled.init();

        //initializing pins
        var moisture = arduino.getPin(A1);
        moisture.setMode(Pin.Mode.ANALOG);

        var button = arduino.getPin(D6);
        button.setMode(Pin.Mode.INPUT);

        var pump = arduino.getPin(D2);
        pump.setMode(Pin.Mode.OUTPUT);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            //watering the plant
            @Override
            public void run() {
                var moistValue = moisture.getValue();
                Oled.getCanvas().drawString(0, 0, "Moisture value:" + moistValue);
                Oled.getCanvas().drawHorizontalLine(0,10,100, MonochromeCanvas.Color.BRIGHT);
                Oled.display();

                try {
                    if (moistValue >= 730) {
                        System.out.println("Plant's dry, it needs water!");
                        pump.setValue(1);
                        Oled.getCanvas().drawHorizontalLine(0,10,100, MonochromeCanvas.Color.BRIGHT);
                        Oled.display();
                        pump.setValue(0);
                    } else if (moistValue < 730 && moistValue > 660)//if the moisture sensor reads anything above this voltage level, then the pump would add water
                    {
                        System.out.println("Plant's pretty wet, still needs water though.");
                        pump.setValue(1);
                        Oled.getCanvas().drawHorizontalLine(0,10,100, MonochromeCanvas.Color.BRIGHT);
                        Oled.display();
                        pump.setValue(0);

                    } else {
                        System.out.println("Plant's  wet, it doesn't need any more water!");
                        Oled.getCanvas().drawHorizontalLine(10,10,100, MonochromeCanvas.Color.BRIGHT);
                        Oled.display();
                        pump.setValue(0);
                    }


                    arduino.addEventListener(new ButtonListener(button, moisture, Oled, arduino, pump));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        };
        timer.scheduleAtFixedRate(task,0,500);
    }
}
