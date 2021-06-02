module core {

    requires spi;
    requires dbconnector;
    requires com.google.gson;
    uses spi.Greeting;
}