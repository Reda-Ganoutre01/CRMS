package ma.location.dao;

import ma.location.model.Client;

public class ClientDAO extends GenericDAO<Client> {

    public ClientDAO() {
        super(Client.class);
    }
}
