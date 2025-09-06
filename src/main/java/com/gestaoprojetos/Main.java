package com.gestaoprojetos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.util.DatabaseConnection;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    
    private Stage primaryStage;
    private ObservableList<Projeto> projetos = FXCollections.observableArrayList();
    private ObservableList<Tarefa> tarefas = FXCollections.observableArrayList();
    private TableView<Projeto> tabelaProjetos;
    private TableView<Tarefa> tabelaTarefas;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        try {
            testarConexaoBanco();
            carregarDados();
            
            primaryStage.setTitle("Sistema de Gestão de Projetos 🚀");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            mostrarTelaLogin();
            primaryStage.show();
            
            System.out.println("✅ Sistema iniciado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar sistema: " + e.getMessage());
            e.printStackTrace();
            mostrarErroInicial(null, e.getMessage());
        }
    }
    
    private void mostrarTelaLogin() {
        VBox layoutLogin = criarTelaLogin();
        primaryStage.setScene(new Scene(layoutLogin));
    }
    
    private VBox criarTelaLogin() {
        Label titulo = new Label("Sistema de Gestão de Projetos");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        TextField campoUsuario = new TextField();
        campoUsuario.setPromptText("Usuário");
        campoUsuario.setMaxWidth(250);
        
        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");
        campoSenha.setMaxWidth(250);
        
        Button btnLogin = new Button("Entrar");
        btnLogin.setDefaultButton(true);
        btnLogin.setOnAction(e -> {
            if (autenticarUsuario(campoUsuario.getText(), campoSenha.getText())) {
                mostrarTelaPrincipal();
            }
        });
        
        VBox layout = new VBox(15, titulo, campoUsuario, campoSenha, btnLogin);
        layout.setStyle("-fx-alignment: center; -fx-padding: 40; -fx-background-color: #f8f9fa;");
        
        return layout;
    }
    
    private void mostrarTelaPrincipal() {
        TabPane abas = new TabPane();
        
        Tab abaProjetos = new Tab("📁 Projetos", criarPainelProjetos());
        abaProjetos.setClosable(false);
        
        Tab abaTarefas = new Tab("✅ Tarefas", criarPainelTarefas());
        abaTarefas.setClosable(false);
        
        Tab abaDashboard = new Tab("📊 Dashboard", criarPainelDashboard());
        abaDashboard.setClosable(false);
        
        abas.getTabs().addAll(abaProjetos, abaTarefas, abaDashboard);
        
        BorderPane layoutPrincipal = new BorderPane();
        layoutPrincipal.setTop(criarCabecalho());
        layoutPrincipal.setCenter(abas);
        
        primaryStage.setScene(new Scene(layoutPrincipal));
    }
    
    private HBox criarCabecalho() {
        Label titulo = new Label("Painel de Controle - Gestão de Projetos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Button btnSair = new Button("Sair");
        btnSair.setOnAction(e -> mostrarTelaLogin());
        
        HBox cabecalho = new HBox(20, titulo, btnSair);
        cabecalho.setStyle("-fx-alignment: center-left; -fx-padding: 15; -fx-background-color: #2c3e50; -fx-text-fill: white;");
        HBox.setHgrow(titulo, Priority.ALWAYS);
        
        return cabecalho;
    }
    
    private VBox criarPainelProjetos() {
        tabelaProjetos = new TableView<>();
        
        TableColumn<Projeto, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Projeto, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        TableColumn<Projeto, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        
        TableColumn<Projeto, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tabelaProjetos.getColumns().addAll(colunaId, colunaNome, colunaDescricao, colunaStatus);
        tabelaProjetos.setItems(projetos);
        
        Button btnNovoProjeto = new Button("➕ Novo Projeto");
        btnNovoProjeto.setOnAction(e -> mostrarDialogoNovoProjeto());
        
        Button btnEditarProjeto = new Button("✏️ Editar");
        btnEditarProjeto.setOnAction(e -> editarProjetoSelecionado());
        
        Button btnExcluirProjeto = new Button("🗑️ Excluir");
        btnExcluirProjeto.setOnAction(e -> excluirProjetoSelecionado());
        
        HBox botoes = new HBox(10, btnNovoProjeto, btnEditarProjeto, btnExcluirProjeto);
        
        VBox painel = new VBox(15, new Label("Gerenciamento de Projetos"), tabelaProjetos, botoes);
        painel.setPadding(new Insets(20));
        
        return painel;
    }
    
    private VBox criarPainelTarefas() {
        tabelaTarefas = new TableView<>();
        
        TableColumn<Tarefa, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Tarefa, String> colunaTitulo = new TableColumn<>("Título");
        colunaTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        
        TableColumn<Tarefa, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        
        TableColumn<Tarefa, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Tarefa, Integer> colunaProjeto = new TableColumn<>("Projeto ID");
        colunaProjeto.setCellValueFactory(new PropertyValueFactory<>("projetoId"));
        
        tabelaTarefas.getColumns().addAll(colunaId, colunaTitulo, colunaDescricao, colunaStatus, colunaProjeto);
        tabelaTarefas.setItems(tarefas);
        
        Button btnNovaTarefa = new Button("➕ Nova Tarefa");
        btnNovaTarefa.setOnAction(e -> mostrarDialogoNovaTarefa());
        
        Button btnEditarTarefa = new Button("✏️ Editar");
        btnEditarTarefa.setOnAction(e -> editarTarefaSelecionada());
        
        Button btnExcluirTarefa = new Button("🗑️ Excluir");
        btnExcluirTarefa.setOnAction(e -> excluirTarefaSelecionada());
        
        HBox botoes = new HBox(10, btnNovaTarefa, btnEditarTarefa, btnExcluirTarefa);
        
        VBox painel = new VBox(15, new Label("Gerenciamento de Tarefas"), tabelaTarefas, botoes);
        painel.setPadding(new Insets(20));
        
        return painel;
    }
    
    private VBox criarPainelDashboard() {
        Label estatisticas = new Label("📊 Dashboard - Em Desenvolvimento");
        estatisticas.setStyle("-fx-font-size: 16px;");
        
        Label totalProjetos = new Label("Total de Projetos: " + projetos.size());
        Label totalTarefas = new Label("Total de Tarefas: " + tarefas.size());
        
        VBox painel = new VBox(15, estatisticas, totalProjetos, totalTarefas);
        painel.setPadding(new Insets(20));
        
        return painel;
    }
    
    private void mostrarDialogoNovoProjeto() {
        Dialog<Projeto> dialog = new Dialog<>();
        dialog.setTitle("Novo Projeto");
        
        ButtonType btnCriar = new ButtonType("Criar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCriar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome do Projeto");
        
        TextArea campoDescricao = new TextArea();
        campoDescricao.setPromptText("Descrição");
        campoDescricao.setPrefRowCount(3);
        
        ComboBox<String> comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll("PLANEJADO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO");
        comboStatus.setValue("PLANEJADO");
        
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(campoNome, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(campoDescricao, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(comboStatus, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCriar) {
                return new Projeto(0, campoNome.getText(), campoDescricao.getText(), null, null, comboStatus.getValue(), null);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(projeto -> {
            salvarProjeto(projeto);
        });
    }
    
    private Tarefa mostrarDialogoNovaTarefa() {
        Dialog<Tarefa> dialog = new Dialog<>();
        dialog.setTitle("Nova Tarefa");
        
        ButtonType btnCriar = new ButtonType("Criar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCriar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Título da Tarefa");
        
        TextArea campoDescricao = new TextArea();
        campoDescricao.setPromptText("Descrição");
        campoDescricao.setPrefRowCount(3);
        
        ComboBox<String> comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll("PENDENTE", "EM_ANDAMENTO", "CONCLUIDA");
        comboStatus.setValue("PENDENTE");
        
        ComboBox<Integer> comboProjeto = new ComboBox<>();
        projetos.forEach(p -> comboProjeto.getItems().add(p.getId()));
        comboProjeto.setPromptText("Selecione o Projeto");
        
        grid.add(new Label("Título:"), 0, 0);
        grid.add(campoTitulo, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(campoDescricao, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(comboStatus, 1, 2);
        grid.add(new Label("Projeto:"), 0, 3);
        grid.add(comboProjeto, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCriar && comboProjeto.getValue() != null) {
                return new Tarefa();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(tarefa -> {
            salvarTarefa(tarefa);
        });
		return null;
    }
    
    private void salvarProjeto(Projeto projeto) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO projetos (nome, descricao, status) VALUES (?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setString(3, projeto.getStatus());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                projeto.setId(rs.getInt(1));
                projetos.add(projeto);
                System.out.println("✅ Projeto criado: " + projeto.getNome());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar projeto: " + e.getMessage());
        }
    }
    
    private void salvarTarefa(Tarefa tarefa) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO tarefas (titulo, descricao, status, projeto_id) VALUES (?, ?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setString(3, tarefa.getStatus());
            stmt.setInt(4, tarefa.getProjeto().getId());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                tarefa.setId(rs.getInt(1));
                tarefas.add(tarefa);
                System.out.println("✅ Tarefa criada: " + tarefa.getTitulo());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar tarefa: " + e.getMessage());
        }
    }
    
    private void editarProjetoSelecionado() {
        Projeto projeto = tabelaProjetos.getSelectionModel().getSelectedItem();
        if (projeto != null) {
            System.out.println("Editar projeto: " + projeto.getNome());
        }
    }
    
    private void excluirProjetoSelecionado() {
        Projeto projeto = tabelaProjetos.getSelectionModel().getSelectedItem();
        if (projeto != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM projetos WHERE id = ?")) {
                
                stmt.setInt(1, projeto.getId());
                stmt.executeUpdate();
                
                projetos.remove(projeto);
                System.out.println("✅ Projeto excluído: " + projeto.getNome());
                
            } catch (SQLException e) {
                System.err.println("❌ Erro ao excluir projeto: " + e.getMessage());
            }
        }
    }
    
    private void editarTarefaSelecionada() {
        Tarefa tarefa = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (tarefa != null) {
            System.out.println("Editar tarefa: " + tarefa.getTitulo());
        }
    }
    
    private void excluirTarefaSelecionada() {
        Tarefa tarefa = tabelaTarefas.getSelectionModel().getSelectedItem();
        if (tarefa != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM tarefas WHERE id = ?")) {
                
                stmt.setInt(1, tarefa.getId());
                stmt.executeUpdate();
                
                tarefas.remove(tarefa);
                System.out.println("✅ Tarefa excluída: " + tarefa.getTitulo());
                
            } catch (SQLException e) {
                System.err.println("❌ Erro ao excluir tarefa: " + e.getMessage());
            }
        }
    }
    
    private void carregarDados() {
        carregarProjetos();
        carregarTarefas();
    }
    
    private void carregarProjetos() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM projetos")) {
            
            projetos.clear();
            while (rs.next()) {
                projetos.add(new Projeto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    null, null, rs.getString("status"), null
                ));
            }
            System.out.println("✅ " + projetos.size() + " projetos carregados");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar projetos: " + e.getMessage());
        }
    }
    
    private void carregarTarefas() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tarefas")) {
            
            tarefas.clear();
            while (rs.next()) {
                tarefas.add(new Tarefa(
                ));
            }
            System.out.println("✅ " + tarefas.size() + " tarefas carregadas");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar tarefas: " + e.getMessage());
        }
    }
    
    private boolean autenticarUsuario(String usuario, String senha) {
        if ("admin".equals(usuario) && "admin123".equals(senha)) {
            return true;
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM usuarios WHERE username = ? AND password = ?")) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Erro na autenticação: " + e.getMessage());
            return false;
        }
    }
    
    private void testarConexaoBanco() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✅ Conexão com MySQL estabelecida!");
            
            criarTabelasSeNecessario(conn);
            
            criarUsuarioAdminPadrao(conn);
            
        } catch (SQLException e) {
            System.err.println("❌ Erro na conexão com banco: " + e.getMessage());
            throw new RuntimeException("Falha na conexão com o banco de dados", e);
        }
    }
    
    private void criarTabelasSeNecessario(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            
            String sqlUsuarios = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            String sqlProjetos = """
                CREATE TABLE IF NOT EXISTS projetos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    descricao TEXT,
                    status VARCHAR(20) DEFAULT 'PLANEJADO',
                    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            
            String sqlTarefas = """
                CREATE TABLE IF NOT EXISTS tarefas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    titulo VARCHAR(100) NOT NULL,
                    descricao TEXT,
                    status VARCHAR(20) DEFAULT 'PENDENTE',
                    projeto_id INT,
                    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlProjetos);
            stmt.execute(sqlTarefas);
            
            System.out.println("✅ Tabelas básicas criadas com sucesso!");
            
            try {
                String sqlAddForeignKey = """
                    ALTER TABLE tarefas 
                    ADD CONSTRAINT fk_tarefas_projeto 
                    FOREIGN KEY (projeto_id) REFERENCES projetos(id) ON DELETE SET NULL
                """;
                stmt.execute(sqlAddForeignKey);
                System.out.println("✅ Foreign key adicionada com sucesso!");
            } catch (SQLException e) {
                System.out.println("ℹ️  Foreign key já existe ou não pode ser adicionada: " + e.getMessage());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao criar tabelas: " + e.getMessage());
            criarTabelasSimplificadas(conn);
        }
    }
    
    private void criarTabelasSimplificadas(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            
            String sqlProjetosSimple = """
                CREATE TABLE IF NOT EXISTS projetos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    descricao TEXT,
                    status VARCHAR(20) DEFAULT 'PLANEJADO'
                )
            """;
            
            String sqlTarefasSimple = """
                CREATE TABLE IF NOT EXISTS tarefas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    titulo VARCHAR(100) NOT NULL,
                    descricao TEXT,
                    status VARCHAR(20) DEFAULT 'PENDENTE',
                    projeto_id INT
                )
            """;
            
            stmt.execute("DROP TABLE IF EXISTS tarefas");
            stmt.execute("DROP TABLE IF EXISTS projetos");
            
            stmt.execute(sqlProjetosSimple);
            stmt.execute(sqlTarefasSimple);
            
            System.out.println("✅ Tabelas simplificadas criadas com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro crítico ao criar tabelas simplificadas: " + e.getMessage());
        }
    }
    
    private void criarUsuarioAdminPadrao(Connection conn) {
        try (PreparedStatement checkStmt = conn.prepareStatement(
                 "SELECT id FROM usuarios WHERE username = 'admin'");
             ResultSet rs = checkStmt.executeQuery()) {
            
            if (!rs.next()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO usuarios (username, password, nome, email) VALUES (?, ?, ?, ?)")) {
                    
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, "admin123");
                    insertStmt.setString(3, "Administrador do Sistema");
                    insertStmt.setString(4, "admin@sistema.com");
                    
                    insertStmt.executeUpdate();
                    System.out.println("✅ Usuário admin padrão criado: admin/admin123");
                }
            } else {
                System.out.println("✅ Usuário admin já existe");
            }
            
        } catch (SQLException e) {
            System.err.println("⚠️  Não foi possível verificar/criar usuário admin: " + e.getMessage());
        }
    }
    
    private void mostrarErroInicial(Stage stage, String mensagemErro) {
        Label erro = new Label("❌ Erro ao iniciar sistema:\n" + mensagemErro);
        erro.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-padding: 20;");
        
        Button btnFechar = new Button("Fechar");
        btnFechar.setOnAction(e -> stage.close());
        
        VBox layoutErro = new VBox(20, erro, btnFechar);
        layoutErro.setStyle("-fx-alignment: center; -fx-padding: 40;");
        
        stage.setScene(new Scene(layoutErro));
        stage.show();
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Gestão de Projetos...");
        launch(args);
    }
}
