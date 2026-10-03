package plant_project;
import org.firmata4j.*;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.IODevice;
import org.firmata4j.I2CDevice;
import java.io.IOException;
public class ButtonListener implements IODeviceEventListener {
    private final Pin buttonPin;
    private final Pin voltagePin;
    private final SSD1306 oledObject;
    private final IODevice arduinoObject;
    private final Pin pumpPin;

    // constructor
    ButtonListener(Pin buttonPin, Pin voltagePin, SSD1306 oledObject, IODevice arduinoObject, Pin pumpPin) {
        this.buttonPin = buttonPin;
        this.voltagePin = voltagePin;
        this.oledObject = oledObject;
        this.arduinoObject = arduinoObject;
        this.pumpPin = pumpPin;
    }

    // Define how onPinChange responds to an event.
    @Override
    public void onPinChange(IOEvent event) {
        // Return right away if the event isn't from the Button.
        if (event.getPin().getIndex() != buttonPin.getIndex()) {
            // to do: return;
            return;
        }
        var voltagePinValue = voltagePin.getValue();
        if (buttonPin.getValue() == 1) {
            oledObject.clear();
            try {
                arduinoObject.stop();
                pumpPin.setValue(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
        // These are empty methods (nothing in the curly braces)
        @Override
        public void onStart(IOEvent event){
        }
        @Override
        public void onStop (IOEvent event){
        }
        @Override
        public void onMessageReceive (IOEvent event, String
        message){
        }
    }
