package com.gestaoprojetos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gestaoprojetos.model.Equipe;
import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.util.DatabaseConnection;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.StringConverter;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
            criarUsuariosTeste();
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
        
        // ✅ ADICIONAR ABA DE EQUIPES
        Tab abaEquipes = new Tab("👥 Equipes", criarPainelEquipes());
        abaEquipes.setClosable(false);
        
        Tab abaProjetos = new Tab("📁 Projetos", criarPainelProjetos());
        abaProjetos.setClosable(false);
        
        Tab abaTarefas = new Tab("✅ Tarefas", criarPainelTarefas());
        abaTarefas.setClosable(false);
        
        Tab abaDashboard = new Tab("📊 Dashboard", criarPainelDashboard());
        abaDashboard.setClosable(false);
        
        // ✅ ADICIONAR ABA DE EQUIPES na lista
        abas.getTabs().addAll(abaDashboard, abaProjetos, abaTarefas, abaEquipes);
        
        BorderPane layoutPrincipal = new BorderPane();
        layoutPrincipal.setTop(criarCabecalho());
        layoutPrincipal.setCenter(abas);
        
        primaryStage.setScene(new Scene(layoutPrincipal));
        primaryStage.show(); // ✅ Garantir que a janela seja mostrada
    }
    
    private VBox criarPainelEquipes() {
        TableView<Equipe> tabelaEquipes = new TableView<>();
        
        TableColumn<Equipe, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaId.setPrefWidth(50);
        
        TableColumn<Equipe, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaNome.setPrefWidth(150);
        
        TableColumn<Equipe, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaDescricao.setPrefWidth(200);
        
        tabelaEquipes.getColumns().addAll(colunaId, colunaNome, colunaDescricao);
        
        // ✅ Carregar equipes do banco
        carregarEquipes(tabelaEquipes);
        
        Button btnNovaEquipe = new Button("➕ Nova Equipe");
        btnNovaEquipe.setOnAction(e -> mostrarDialogoNovaEquipe(tabelaEquipes));
        
        Button btnGerenciarMembros = new Button("👥 Gerenciar Membros");
        btnGerenciarMembros.setOnAction(e -> gerenciarMembrosEquipe(tabelaEquipes));
        
        HBox botoes = new HBox(10, btnNovaEquipe, btnGerenciarMembros);
        botoes.setPadding(new Insets(10));
        
        VBox painel = new VBox(15, new Label("Gerenciamento de Equipes"), tabelaEquipes, botoes);
        painel.setPadding(new Insets(20));
        
        return painel;
    }
    
    private void carregarEquipes(TableView<Equipe> tabelaEquipes) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equipe")) {
            
            ObservableList<Equipe> equipes = FXCollections.observableArrayList();
            while (rs.next()) {
                equipes.add(new Equipe(
                    rs.getInt("id_equipe"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                ));
            }
            tabelaEquipes.setItems(equipes);
            
        } catch (SQLException e) {
            System.err.println("Erro ao carregar equipes: " + e.getMessage());
        }
    }

    private void mostrarDialogoNovaEquipe(TableView<Equipe> tabelaEquipes) {
        Dialog<Equipe> dialog = new Dialog<>();
        dialog.setTitle("Nova Equipe");
        
        ButtonType btnCriar = new ButtonType("Criar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCriar, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome da Equipe");
        
        TextArea campoDescricao = new TextArea();
        campoDescricao.setPromptText("Descrição");
        campoDescricao.setPrefRowCount(3);
        
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(campoNome, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(campoDescricao, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCriar) {
                return new Equipe(0, campoNome.getText(), campoDescricao.getText());
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(equipe -> {
            salvarEquipe(equipe, tabelaEquipes);
        });
    }

    private void salvarEquipe(Equipe equipe, TableView<Equipe> tabelaEquipes) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO equipe (nome, descricao) VALUES (?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                equipe.setId(rs.getInt(1));
                tabelaEquipes.getItems().add(equipe);
                System.out.println("✅ Equipe criada: " + equipe.getNome());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar equipe: " + e.getMessage());
        }
    }

    private void gerenciarMembrosEquipe(TableView<Equipe> tabelaEquipes) {
        Equipe equipeSelecionada = tabelaEquipes.getSelectionModel().getSelectedItem();
        if (equipeSelecionada != null) {
            mostrarDialogoMembrosEquipe(equipeSelecionada);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Nenhuma equipe selecionada");
            alert.setContentText("Por favor, selecione uma equipe para gerenciar seus membros.");
            alert.showAndWait();
        }
    }
    
    private HBox criarCabecalho() {
        Label titulo = new Label("Sistema de Gestão de Projetos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // ✅ Label para mostrar o usuário logado
        Label labelUsuario = new Label("Usuário: " + (usuarioLogado != null ? usuarioLogado : "Convidado"));
        labelUsuario.setStyle("-fx-text-fill: white;");
        
        Button btnSair = new Button("🚪 Sair");
        btnSair.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnSair.setOnAction(e -> mostrarTelaLogin());
        
        HBox cabecalho = new HBox(20, titulo, labelUsuario, btnSair);
        cabecalho.setStyle("-fx-alignment: center; -fx-padding: 15; -fx-background-color: #2c3e50;");
        HBox.setHgrow(titulo, Priority.ALWAYS);
        
        return cabecalho;
    }
    
    private void gerenciarEquipes() {
        // Aqui você pode abrir um dialog ou nova cena para gerenciar equipes
        System.out.println("Abrir gerenciamento de equipes...");
        
        // Exemplo simples:
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gerenciamento de Equipes");
        alert.setHeaderText("Funcionalidade em Desenvolvimento");
        alert.setContentText("Em breve você poderá gerenciar equipes aqui!");
        alert.showAndWait();
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
    
    private void mostrarDialogoNovaTarefa() {
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
        
        ComboBox<Projeto> comboProjeto = new ComboBox<>(); // ✅ Mudar para Projeto, não Integer
        comboProjeto.setItems(FXCollections.observableArrayList(projetos));
        comboProjeto.setPromptText("Selecione o Projeto");
        comboProjeto.setConverter(new StringConverter<Projeto>() {
            @Override
            public String toString(Projeto projeto) {
                return projeto != null ? projeto.getNome() : "";
            }
            
            @Override
            public Projeto fromString(String string) {
                return null;
            }
        });
        
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
                // ✅ AGORA cria a tarefa CORRETAMENTE com o projeto
                Tarefa tarefa = new Tarefa();
                tarefa.setTitulo(campoTitulo.getText());
                tarefa.setDescricao(campoDescricao.getText());
                tarefa.setStatus(comboStatus.getValue().toLowerCase());
                tarefa.setProjeto(comboProjeto.getValue()); // ✅ Projeto não é mais null!
                return tarefa;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(tarefa -> {
            salvarTarefa(tarefa);
        });
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
        // ✅ DEBUG: Verificar o que está chegando
        System.out.println("Tentando login: " + usuario + "/" + senha);
        
        if ("admin".equals(usuario) && "admin123".equals(senha)) {
            System.out.println("✅ Login admin bem-sucedido");
            return true;
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM usuarios WHERE username = ? AND password = ?")) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            
            ResultSet rs = stmt.executeQuery();
            boolean autenticado = rs.next();
            
            System.out.println("✅ Login BD: " + autenticado);
            return autenticado;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro na autenticação: " + e.getMessage());
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
    
    private void criarUsuariosTeste() {
        try {
            // Verificar se os usuários já existem
            if (!usuarioExiste("admin") && !usuarioExiste("gerente") && !usuarioExiste("colaborador")) {
                
                // Criar administrador
                criarUsuario("Administrador Sistema", "12345678901", "admin@empresa.com", 
                            "Administrador", "admin", "admin123", "administrador");
                
                // Criar gerente
                criarUsuario("Gerente Projetos", "12345678902", "gerente@empresa.com", 
                            "Gerente de Projetos", "gerente", "gerente123", "gerente");
                
                // Criar colaborador
                criarUsuario("Colaborador Teste", "12345678903", "colaborador@empresa.com", 
                            "Desenvolvedor", "colaborador", "colab123", "colaborador");
                
                System.out.println("✅ Usuários de teste criados com sucesso!");
            }
        } catch (Exception e) {
            System.err.println("⚠️  Não foi possível criar usuários de teste: " + e.getMessage());
        }
    }

    private boolean usuarioExiste(String login) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM usuario WHERE login = ?")) {
            
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void criarUsuario(String nome, String cpf, String email, String cargo, 
                             String login, String senha, String perfil) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO usuario (nome_completo, cpf, email, cargo, login, senha, perfil) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, email);
            stmt.setString(4, cargo);
            stmt.setString(5, login);
            stmt.setString(6, senha);
            stmt.setString(7, perfil);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao criar usuário " + login + ": " + e.getMessage());
        }
    }
}
