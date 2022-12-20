module com.udacity.catpoint.security {
    requires java.desktop;
    requires com.udacity.catpoint.image;
    requires miglayout;
    requires guava;
    requires java.prefs;
    requires com.google.gson;
    opens com.udacity.catpoint.security.data to com.google.gson;
}