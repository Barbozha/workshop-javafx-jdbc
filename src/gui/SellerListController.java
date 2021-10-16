package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service; // Princípio de injeção de dependência

	// Criar referências da tela do departmentList(botão,tabelview e as duas colunas
	// da tableview)
	@FXML
	private TableView<Seller> tableViewSellers;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;// (<Nome da tabela, tipo da coluna(id)>) e no nome da coluna
															// que eu dou para ela (tableColumnId)
	@FXML
	private TableColumn<Seller, String> tableColumnName;// (Refere-se a coluna do name)
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew; // É o botão do ToolBar chamado new

	// Carrego os meus departamentos nesta observableList
	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) { // Evento do botão new
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	// Injeto a Dependência
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	// Para fazer a tabela funcionar temos que fazer um macetezinho no método
	// initialize, veja a abaixo.
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Vamos criar uma método auxiliar
		initializeNodes(); // para iniciar alguns componentes da minha tela

	}

	private void initializeNodes() {
		// Colocaremos alguns comandos para iniciar apropriadamente os comportamentos
		// das minhas colunas na tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

		// Para fazer a área dos dados da tabela chegrem até o fim da minha janela do
		// aplicativo
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSellers.prefHeightProperty().bind(stage.heightProperty());

	}

	// Este método será responsável por acessar o serviço
	// Carregar os departamentos
	// e jogar os departamentos na minha observableList
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("The Service was null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSellers.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// Injetando o Seller obj lá no controlador na tela do formulário
			// Pegando uma referência para o controlador
			SellerFormController controller = loader.getController();

			// Injetando no controlador, o Seller
			controller.setSeller(obj);
			controller.setSellerService(new SellerService());

			// Increvo o meu objeto SellerListController pra ser um observer do evento
			// onDataChanged()
			controller.subscribeDataChangeListener(this);

			// Pegando o meu controlador e chamando o meu updateFormData()
			// Para carregar o obj no formulário.
			controller.updateFormData();

			// Abrindo a janela de cadastro de Seller
			// de forma modal
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	// Quando os dados forem alterados, emite uma notificação para que seja
	// atualizar
	// os dados da tabela department.(Design pattern Observer)
	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

			private void removeEntity(Seller obj) {
				Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
				
				
				if(result.get() == ButtonType.OK) {
					if (service == null) {
						throw new IllegalStateException("Service was null");
					}
					try {
						service.remove(obj);
						updateTableView();
					}
					catch(DbIntegrityException e) {
						Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
					}
						
				}
			}
}

