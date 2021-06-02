module greetings {

    requires spi;
    provides spi.Greeting with plugins.SwedishGreeting, plugins.EnglishGreeting;
}