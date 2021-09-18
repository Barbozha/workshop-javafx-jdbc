package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable{

	
	// Criar referências da tela do departmentList(botão,tabelview e as duas colunas da tableview)
	@FXML
	private TableView<Department> tableViewDepartments;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;//(<Nome da tabela, tipo da coluna(id)>) e no nome da coluna que eu dou para ela (tableColumnId)
	@FXML
	private TableColumn<Department, String> tableColumnName;//(Refere-se a coluna do name)
	
	@FXML
	private Button btNew; //É o botão do ToolBar chamado new
	
	@FXML
	public void onBtNewAction() { // Evento do botão new
		System.out.println("onBtNewAction");
	}
	
	//Para fazer a tabela funcionar temos que fazer um macetezinho no método initialize, veja a abaixo.
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		//Vamos criar uma método auxiliar
		initializeNodes(); // para iniciar alguns componentes da minha tela
		
	}

	private void initializeNodes() {
		// Colocaremos alguns comandos para iniciar apropriadamente os comportamentos das minhas colunas na tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//Para fazer a área dos dados da tabela chegrem até o fim da minha janela do aplicativo
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());
		
	}

}
