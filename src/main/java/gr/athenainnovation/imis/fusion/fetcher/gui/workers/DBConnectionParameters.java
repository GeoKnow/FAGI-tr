package gr.athenainnovation.imis.fusion.fetcher.gui.workers;

/**
 * Container object holding database connection parameters.
 * @author Thomas Maroulis
 */
public class DBConnectionParameters {
    private final String url, username, password;

    /**
     * 
     * @param url database url
     * @param username database username
     * @param password database password
     */
    public DBConnectionParameters(final String url, final String username, final String password) {
        if(url == null || url.isEmpty()) {
            throw new IllegalArgumentException("DB url cannot be null or empty.");
        }
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("DB username cannot be null or empty.");
        }
        if(password == null || password.isEmpty()) {
            throw new IllegalArgumentException("DB password cannot be null or empty.");
        }
        
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     *
     * @return database url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @return database username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return database password
     */
    public String getPassword() {
        return password;
    }
}