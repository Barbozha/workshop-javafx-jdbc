package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
	private DepartmentService service; //Princ�pio de inje��o de depend�ncia
	
	// Criar refer�ncias da tela do departmentList(bot�o,tabelview e as duas colunas da tableview)
	@FXML
	private TableView<Department> tableViewDepartments;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;//(<Nome da tabela, tipo da coluna(id)>) e no nome da coluna que eu dou para ela (tableColumnId)
	@FXML
	private TableColumn<Department, String> tableColumnName;//(Refere-se a coluna do name)
	
	@FXML
	private Button btNew; //� o bot�o do ToolBar chamado new
	
	//Carrego os meus departamentos nesta observableList
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) { // Evento do bot�o new
		Stage parentStage = Utils.currentStage(event);
		createDialogForm("/gui/DepartmentForm.fxml", parentStage);
	}
	
	//Injeto a Depend�ncia
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	//Para fazer a tabela funcionar temos que fazer um macetezinho no m�todo initialize, veja a abaixo.
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		//Vamos criar uma m�todo auxiliar
		initializeNodes(); // para iniciar alguns componentes da minha tela
		
	}

	private void initializeNodes() {
		// Colocaremos alguns comandos para iniciar apropriadamente os comportamentos das minhas colunas na tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//Para fazer a �rea dos dados da tabela chegrem at� o fim da minha janela do aplicativo
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	//Este m�todo ser� respons�vel por acessar o servi�o
	//Carregar os departamentos
	//e jogar os departamentos na minha observableList
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("The Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartments.setItems(obsList);
	}
	
	private void createDialogForm(String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			//Abrindo a janela de cadastro de Department
			//de forma modal
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
