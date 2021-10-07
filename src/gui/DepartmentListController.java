package gui;

import java.io.IOException;
import java.net.URL;
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
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service; // Princípio de injeção de dependência

	// Criar referências da tela do departmentList(botão,tabelview e as duas colunas
	// da tableview)
	@FXML
	private TableView<Department> tableViewDepartments;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;// (<Nome da tabela, tipo da coluna(id)>) e no nome da coluna
															// que eu dou para ela (tableColumnId)
	@FXML
	private TableColumn<Department, String> tableColumnName;// (Refere-se a coluna do name)

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew; // É o botão do ToolBar chamado new

	// Carrego os meus departamentos nesta observableList
	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) { // Evento do botão new
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	// Injeto a Dependência
	public void setDepartmentService(DepartmentService service) {
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

		// Para fazer a área dos dados da tabela chegrem até o fim da minha janela do
		// aplicativo
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());

	}

	// Este método será responsável por acessar o serviço
	// Carregar os departamentos
	// e jogar os departamentos na minha observableList
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("The Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartments.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// Injetando o Department obj lá no controlador na tela do formulário
			// Pegando uma referência para o controlador
			DepartmentFormController controller = loader.getController();

			// Injetando no controlador, o Department
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());

			// Increvo o meu objeto DepartmentListController pra ser um observer do evento
			// onDataChanged()
			controller.subscribeDataChangeListener(this);

			// Pegando o meu controlador e chamando o meu updateFormData()
			// Para carregar o obj no formulário.
			controller.updateFormData();

			// Abrindo a janela de cadastro de Department
			// de forma modal
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
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

			private void removeEntity(Department obj) {
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

