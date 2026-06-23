package fr.maa.models;

public class Client {

    private int id;
    private String pseudo;
    private String nom;
    private String prenom;
    private String numero;
    private String email;
    private String password;
    private String adresse;
    private Role role = Role.CLIENT;

    public Client() {}

    public Client(int id, String pseudo, String nom, String prenom, String numero, String email, String password, String adresse, boolean isAdmin) {
        this.id = id;
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.numero = numero;
        this.email = email;
        this.password = password;
        this.adresse = adresse;
        this.role = isAdmin ? Role.ADMIN : Role.CLIENT;
    }

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role == null ? Role.CLIENT : role; }

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isVendeur() { return role == Role.VENDEUR; }

    /**
     * Conservé pour compatibilité avec le code existant (formulaire client,
     * inscription). Activer "admin" promeut au rôle ADMIN ; le désactiver
     * rétrograde en CLIENT, sauf si l'utilisateur est VENDEUR (rôle préservé).
     */
    public void setAdmin(boolean admin) {
        if (admin) {
            this.role = Role.ADMIN;
        } else if (this.role == Role.ADMIN) {
            this.role = Role.CLIENT;
        }
    }
}
