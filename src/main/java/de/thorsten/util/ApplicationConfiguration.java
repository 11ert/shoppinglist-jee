/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.thorsten.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 * @author Thorsten
 */
@Singleton
public class ApplicationConfiguration implements Serializable {

    @Inject
    private Logger log;

    private Manifest manifest = null;

    private String applicationBuildVersion;

    private String applicationBuildTimestamp;
    
    private String developer;

    @PostConstruct
    public void init() {
        log.fine("ApplicationConfiguration - initializing Application....");
        File manifestFile = null;
        try {
            InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/META-INF/MANIFEST.MF");
            manifest = new Manifest();
            manifest.read(is);

            Attributes atts = manifest.getMainAttributes();

            this.applicationBuildVersion = atts.getValue("Implementation-Build");
            this.applicationBuildTimestamp = atts.getValue("Implementation-Timestamp");
            this.developer = atts.getValue("Developer");

            log.fine("ApplicationConfiguration.applicationBuildVersion: " + this.applicationBuildVersion);
            log.fine("ApplicationConfiguration.applicationBuildTimestamp: " + this.applicationBuildTimestamp);
            log.fine("ApplicationConfiguration.developer: " + this.developer);
        } catch (IOException ioe) {
            log.warning("Unable to read the Manifest file from '" + manifestFile.getAbsolutePath() + "'");
        }
    }

    public Manifest getManifest() {
        return manifest;
    }

    /**
     * @return the applicationBuildVersion
     */
    @Produces
    @Named
    public String getApplicationBuildVersion() {
        return applicationBuildVersion;
    }

    /**
     * @return the applicationBuildTimestamp
     */
    @Produces
    @Named
    public String getApplicationBuildTimestamp() {
        return applicationBuildTimestamp;
    }

    /**
     * @return the developer
     */
    @Produces
    @Named
    public String getDeveloper() {
        return developer;
    }

}
