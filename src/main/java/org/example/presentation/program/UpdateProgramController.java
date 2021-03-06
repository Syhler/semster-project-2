package org.example.presentation.program;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.App;
import org.example.domain.applicationFacade.DomainFacade;
import org.example.domain.buisnessComponents.*;
import org.example.presentation.utilities.ControllerUtility;
import org.example.presentation.multipleLanguages.LanguageHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateProgramController implements Initializable {

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button cancelBtn;
    @FXML
    private Label updateProgramTitle;
    @FXML
    private TextArea updateInsertTitle;
    @FXML
    private Label updateProgramDescription;
    @FXML
    private TextArea updateInsertDescription;
    @FXML
    private Label updateProgramCompany;
    @FXML
    private Label updateProgramCredits;
    @FXML
    private Button updateCreditBtn;
    @FXML
    private Button updateProgramBtn;
    @FXML
    private Label remainingCharactersDesc;
    @FXML
    private Label remainingCharactersTitle;
    @FXML
    private Label updateProgramProducer;
    @FXML
    private ComboBox<Company> chooseCompany;
    @FXML
    private ComboBox<User> chooseProducer;
    @FXML
    private TextArea creditList;
    @FXML
    private TextArea producerList;
    @FXML
    private Button addSelectedProducer;
    @FXML
    private Label addCreditHeader;
    @FXML
    private ComboBox<Credit> chooseCredit;
    @FXML
    private Button addCreditButton;

    private int maxSizeTitle = 100;
    private int maxSizeDesc = 1000;
    private List<User> producers = new ArrayList<>();
    private List<Credit> credits = new ArrayList<>();
    private Program globalProgram;

    private DomainFacade domainHandler = new DomainFacade();


    @FXML
    private void closeUpdateProgram(ActionEvent event)
    {
        ControllerUtility.closeProgram(event);
    }

    /**
     * Opens "updateProgram.fxml" as a popup scene.
     * @param program of the program that you want to open
     * @return a programEntity with its different variables filled.
     */
    public Program openView(Program program, ActionEvent event) throws IOException {

        FXMLLoader loader = null;
        loader = App.getLoader("updateProgram");
        Parent node = loader.load();
        UpdateProgramController updateProgramController = loader.<UpdateProgramController>getController();

        updateProgramController.globalProgram = program;

        if (program != null) {
            updateProgramController.updateInsertTitle.setText(program.getProgramInformation().getTitle());
            updateProgramController.updateInsertDescription.setText(program.getProgramInformation().getDescription());


            if (program.getProducers() != null) {
                for (int i = 0; i < program.getProducers().size(); i++) {
                    updateProgramController.producerList.appendText(program.getProducers().get(i).getName() + "\n");
                }
            }

            if (program.getCredits() != null) {
                for (int i = 0; i < program.getCredits().size(); i++) {
                    var user = program.getCredits().get(i).getUser();
                    updateProgramController.creditList.appendText(user.getFullName() +  ": " + user.getTitle()  +"\n");
                }
            }
        }

        Scene scene = new Scene(node);

        Stage stage = new Stage();
        stage.setTitle(LanguageHandler.getText("updateProgramStageTitle"));
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("loginImages/tv2trans.png")));
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getTarget()).getScene().getWindow());

        stage.showAndWait();

        return updateProgramController.globalProgram;
    }

    /**
     * Calculates, updates and inserts how many characters the user has remaining in the description textArea
     */
    @FXML
    private void remainingCharactersDesc()
    {
        updateInsertDescription.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                remainingCharactersDesc.setText(ControllerUtility.remainingCharacters(updateInsertDescription, maxSizeDesc));
            }
        });
    }

    /**
     * Calculates, updates and inserts how many characters the user has remaining in the title textArea
     */
    @FXML
    private void remainingCharactersTitle()
    {
        updateInsertTitle.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                remainingCharactersTitle.setText(ControllerUtility.remainingCharacters(updateInsertTitle, maxSizeTitle));
            }
        });
    }


    /**
     * Enables user to choose a company from a comboBox
     */
    public void getCompanies()
    {
        var companies = domainHandler.getAllCompanies();
        chooseCompany.getItems().addAll(companies);

        Platform.runLater(() ->
        {
            Callback<ListView<Company>, ListCell<Company>> cellFactory = new Callback<>() {

                @Override
                public ListCell<Company> call(ListView<Company> l) {
                    return new ListCell<>() {

                        @Override
                        protected void updateItem(Company item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getName());
                            }
                        }
                    };
                }
            };

            chooseCompany.setCellFactory(cellFactory);
            chooseCompany.setButtonCell(cellFactory.call(null));
        });
    }


    /**
     * Enables user to choose a producer from a comboBox
     */
    public void getProducers()
    {
        var producers = domainHandler.getUserByRole(Role.Producer);

        Platform.runLater(() -> {
            chooseProducer.getItems().addAll(producers);

            Callback<ListView<User>, ListCell<User>> cellFactory = new Callback<>() {

                @Override
                public ListCell<User> call(ListView<User> user) {
                    return new ListCell<>() {

                        @Override
                        protected void updateItem(User item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getFullName());
                            }
                        }
                    };
                }
            };

            chooseProducer.setCellFactory(cellFactory);
            chooseProducer.setButtonCell(cellFactory.call(null));
        });
    }

    /**
     * Enables user to choose a credit from a comboBox
     */
    public void getCredits()
    {
        var actors = getActorsFromDatabase();

        Platform.runLater(() ->{
            chooseCredit.getItems().addAll(actors);

            Callback<ListView<Credit>, ListCell<Credit>> cellFactory = new Callback<>() {

                @Override
                public ListCell<Credit> call(ListView<Credit> l) {
                    return new ListCell<Credit>() {

                        @Override
                        protected void updateItem(Credit item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                setText(item.getUser().getFullName() + ": " + item.getUser().getTitle());
                            }
                        }
                    };
                }
            };
            chooseCredit.setCellFactory(cellFactory);
            chooseCredit.setButtonCell(cellFactory.call(null));
        });
    }

    /**
     * Adds the selected producer from the combobox, to a list and a text area
     * @param event
     */
    public void addProducer(ActionEvent event)
    {
        User producer = chooseProducer.getSelectionModel().getSelectedItem();

        producerList.appendText(producer.getFullName()+"\n");

        producers.add(chooseProducer.getSelectionModel().getSelectedItem());
    }

    /**
     * Test method, should be deleted. Creates some CreditEntity and put them in a list
     * @return list of CreditEntity
     */
    public List<Credit> getActorsFromDatabase()
    {
        List<Credit> credits = new ArrayList<>();
        var actors = domainHandler.getUserByRole(Role.Actor);
        for (User actor: actors) {
            credits.add(new Credit(actor));
        }

        return credits;
    }



    /**
     * Adds the selected credit from the combobox, to a list and a text area
     * @param event
     */
    public void addCredit(ActionEvent event)
    {
        Credit credit = chooseCredit.getSelectionModel().getSelectedItem();

        creditList.appendText(credit.getUser().getFullName() + ": " + credit.getUser().getTitle()+"\n");

        credits.add(chooseCredit.getSelectionModel().getSelectedItem());
    }

    /**
     * On the click of a button, opens a scene where a credit can be made and adds the made credit to a list and text area
     * @param event
     * @throws IOException
     */
    @FXML
    public void goToCreateCredit(ActionEvent event) throws IOException {
        CreditController creditController = new CreditController();
        User credit = creditController.openView(event);

        if (credit == null) return;

        creditList.appendText(credit.getFullName() + ": " + credit.getTitle()+"\n");
        Credit creditEntity = new Credit(0,credit);
        credits.add(creditEntity);
    }


    /**
     * Updates the different objects in a program
     * @param event
     */
    @FXML
    public void updateProgram(ActionEvent event)
    {
        progressIndicator.setVisible(true);
        var thread = new Thread(() ->
        {
            Company company = chooseCompany.getSelectionModel().getSelectedItem();

            String title = updateInsertTitle.getText();

            String description = updateInsertDescription.getText();;

            //globalProgram = new Program(programId, title, description, company, producers, credits);

            globalProgram.getProgramInformation().setTitle(title);
            globalProgram.getProgramInformation().setDescription(description);
            globalProgram.setCompany(company);
            globalProgram.setProducers(producers);
            globalProgram.setCredits(credits);
            globalProgram.update();

            Platform.runLater(()->closeUpdateProgram(event));
        });
        thread.setDaemon(true);
        thread.start();

    }


    private Runnable getDataFromDatabase()
    {
        return () ->
        {
            Platform.runLater(() -> {
                chooseCompany.setPromptText("Loading...");
                chooseProducer.setPromptText("Loading...");
                chooseCredit.setPromptText("Loading...");
            });

            getCompanies();
            Platform.runLater(() -> chooseCompany.getSelectionModel().selectFirst());
            getProducers();
            Platform.runLater(() -> chooseProducer.getSelectionModel().selectFirst());
            getCredits();
            Platform.runLater(() -> chooseCredit.getSelectionModel().selectFirst());
        };
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        creditList.setEditable(false);
        producerList.setEditable(false);

        var thread = new Thread(getDataFromDatabase());
        thread.start();

        remainingCharactersDesc();
        remainingCharactersDesc.setText(ControllerUtility.remainingCharacters(updateInsertDescription, maxSizeDesc));
        ControllerUtility.maxTextSize(updateInsertDescription, maxSizeDesc);

        remainingCharactersTitle();
        remainingCharactersTitle.setText(ControllerUtility.remainingCharacters(updateInsertTitle, maxSizeTitle));
        ControllerUtility.maxTextSize(updateInsertTitle, maxSizeTitle);

        addSelectedProducer.setText(LanguageHandler.getText("add"));
        updateProgramBtn.setText(LanguageHandler.getText("updateProgram"));
        cancelBtn.setText(LanguageHandler.getText("cancel"));
        updateProgramTitle.setText(LanguageHandler.getText("titleHeader"));
        updateInsertTitle.setPromptText(LanguageHandler.getText("insertTitle"));
        updateProgramDescription.setText(LanguageHandler.getText("descriptionHeader"));
        updateInsertDescription.setPromptText(LanguageHandler.getText("insertDescription"));
        updateProgramCompany.setText(LanguageHandler.getText("programCompany"));
        updateProgramCredits.setText(LanguageHandler.getText("programCredits"));
        updateCreditBtn.setText(LanguageHandler.getText("createCreditStageTitle"));
        updateProgramProducer.setText(LanguageHandler.getText("producer"));
        addCreditButton.setText(LanguageHandler.getText("add"));
        addCreditHeader.setText(LanguageHandler.getText("addCreditHeader"));

    }
}
