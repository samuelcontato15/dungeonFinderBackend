package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
import com.pi4.dungeonFinderBackend.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class JogoServiceTest {

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID adminId;
    private UUID usuarioComumId;

    @BeforeEach
    void setUp() {
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        Usuario comum = new Usuario();
        comum.setNick("comum_jogo");
        comum.setEmail("comum@jogo.com");
        comum.setSenhaHash("123");
        usuarioComumId = usuarioService.criar(comum).getId();
    }

    private Jogo criarJogoBase(String nome, String slug) {
        Jogo jogo = new Jogo();
        jogo.setNome(nome);
        jogo.setSlug(slug);
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.RPG);
        return jogoService.criar(jogo, adminId);
    }

    @Test
    void criar_comSucesso() {
        Jogo jogo = criarJogoBase("Dark Souls", "dark-souls");
        assertThat(jogo.getId()).isNotNull();
        assertThat(jogo.getNome()).isEqualTo("Dark Souls");
        assertThat(jogo.getCriadoEm()).isNotNull();
    }

    @Test
    void criar_nomeDuplicado_lancaExcecao() {
        criarJogoBase("Dark Souls", "dark-souls");
        Jogo duplicado = new Jogo();
        duplicado.setNome("Dark Souls");
        duplicado.setSlug("dark-souls-2");
        duplicado.setCapa("");
        duplicado.setCategoria(CategoriaJogo.RPG);
        assertThatThrownBy(() -> jogoService.criar(duplicado, adminId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Nome já cadastrado");
    }

    @Test
    void criar_slugDuplicado_lancaExcecao() {
        criarJogoBase("Dark Souls", "dark-souls");
        Jogo duplicado = new Jogo();
        duplicado.setNome("Dark Souls 2");
        duplicado.setSlug("dark-souls");
        duplicado.setCapa("");
        duplicado.setCategoria(CategoriaJogo.RPG);
        assertThatThrownBy(() -> jogoService.criar(duplicado, adminId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Slug já cadastrado");
    }

    @Test
    void criar_porNaoAdmin_lancaExcecao() {
        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Proibido");
        jogo.setSlug("jogo-proibido");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.RPG);
        assertThatThrownBy(() -> jogoService.criar(jogo, usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }

    @Test
    void buscarPorId_comSucesso() {
        Jogo criado = criarJogoBase("Elden Ring", "elden-ring");
        Jogo encontrado = jogoService.buscarPorId(criado.getId());
        assertThat(encontrado.getNome()).isEqualTo("Elden Ring");
    }

    @Test
    void buscarPorId_inexistente_lancaExcecao() {
        assertThatThrownBy(() -> jogoService.buscarPorId(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Jogo não encontrado");
    }

    @Test
    void listarTodos_retornaJogos() {
        criarJogoBase("Jogo A", "jogo-a");
        criarJogoBase("Jogo B", "jogo-b");
        List<Jogo> jogos = jogoService.listarTodos();
        assertThat(jogos.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void atualizar_comSucesso() {
        Jogo criado = criarJogoBase("Old Name", "old-slug");
        Jogo novos = new Jogo();
        novos.setNome("New Name");
        novos.setSlug("new-slug");
        novos.setCapa("nova-capa.jpg");
        novos.setCategoria(CategoriaJogo.MMO);
        Jogo atualizado = jogoService.atualizar(criado.getId(), novos, adminId);
        assertThat(atualizado.getNome()).isEqualTo("New Name");
        assertThat(atualizado.getCategoria()).isEqualTo(CategoriaJogo.MMO);
    }

    @Test
    void atualizar_porNaoAdmin_lancaExcecao() {
        Jogo criado = criarJogoBase("Jogo Update", "jogo-update");
        Jogo novos = new Jogo();
        novos.setNome("Novo Nome");
        novos.setSlug("novo-slug");
        novos.setCapa("");
        novos.setCategoria(CategoriaJogo.RPG);
        assertThatThrownBy(() -> jogoService.atualizar(criado.getId(), novos, usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }

    @Test
    void deletar_comSucesso() {
        Jogo criado = criarJogoBase("Jogo Delete", "jogo-delete");
        UUID id = criado.getId();
        jogoService.deletar(id, adminId);
        assertThatThrownBy(() -> jogoService.buscarPorId(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Jogo não encontrado");
    }

    @Test
    void deletar_porNaoAdmin_lancaExcecao() {
        Jogo criado = criarJogoBase("Jogo Delete Proibido", "jogo-delete-proibido");
        assertThatThrownBy(() -> jogoService.deletar(criado.getId(), usuarioComumId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }
}