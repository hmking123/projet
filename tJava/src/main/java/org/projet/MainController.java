package org.projet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.projet.jpa.user;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class MainController {
    @FXML
    private TableView<user> userTable;
    @FXML
    private TableColumn<user, Long> idColumn;
    @FXML
    private TableColumn<user, String> nameColumn;
    @FXML
    private TableColumn<user, String> adresseColumn;
    @FXML
    private TableColumn<user, Date> naissanceColumn;
    @FXML
    private TableColumn<user, Integer> telephoneColumn;
    @FXML
    private TableColumn<user, String> emailColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField adresseField;
    @FXML
    private DatePicker naissanceField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("user");
    private user selectedUser;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        naissanceColumn.setCellValueFactory(new PropertyValueFactory<>("naissance"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadUsers();

        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUser = newValue;
                nameField.setText(selectedUser.getName());
                adresseField.setText(selectedUser.getAdresse());
                if (selectedUser.getNaissance() != null) {
                    naissanceField.setValue(toLocalDate(selectedUser.getNaissance()));
                } else {
                    naissanceField.setValue(null);
                }
                telephoneField.setText(String.valueOf(selectedUser.getTelephone()));
                emailField.setText(selectedUser.getEmail());
            }
        });


        Scene scene = userTable.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("/file.css").toExternalForm());
        }
    }

    private void loadUsers() {
        EntityManager em = emf.createEntityManager();
        ObservableList<user> users = FXCollections.observableArrayList(em.createQuery("from user", user.class).getResultList());
        userTable.setItems(users);
        em.close();
    }

    @FXML
    private void addUser() {
        String name = nameField.getText();
        String adresse = adresseField.getText();
        Date naissance = toDate(naissanceField.getValue());
        Integer telephone = parsePhone(telephoneField.getText());
        String email = emailField.getText();

        if (name.isEmpty() || adresse.isEmpty() || naissance == null || telephone == null || email.isEmpty()) {
            return;
        }

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        user user = new user();
        user.setName(name);
        user.setAdresse(adresse);
        user.setNaissance(naissance);
        user.setTelephone(telephone);
        user.setEmail(email);
        em.persist(user);
        em.getTransaction().commit();
        em.close();

        loadUsers();
        clearFields();
    }

    @FXML
    private void updateUser() {
        if (selectedUser != null) {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            selectedUser.setName(nameField.getText());
            selectedUser.setAdresse(adresseField.getText());
            selectedUser.setNaissance(toDate(naissanceField.getValue()));
            selectedUser.setTelephone(parsePhone(telephoneField.getText()));
            selectedUser.setEmail(emailField.getText());
            em.merge(selectedUser);
            em.getTransaction().commit();
            em.close();

            loadUsers();
            clearFields();
        }
    }

    @FXML
    private void deleteUser() {
        if (selectedUser != null) {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            user user = em.find(user.class, selectedUser.getId());
            if (user != null) {
                em.remove(user);
                em.getTransaction().commit();
            }
            em.close();

            loadUsers();
            clearFields();
        }
    }

    private void clearFields() {
        nameField.clear();
        adresseField.clear();
        naissanceField.setValue(null);
        telephoneField.clear();
        emailField.clear();
        selectedUser = null;
    }

    private Date toDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private Integer parsePhone(String phoneStr) {
        try {
            return Integer.valueOf(phoneStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
