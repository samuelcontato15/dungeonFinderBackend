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
class JogoFavoritoServiceTest {

    @Autowired
    private JogoFavoritoService jogoFavoritoService;

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID adminId;
    private UUID usuarioId;
    private UUID jogoId;

    @BeforeEach
    void setUp() {
        adminId = usuarioRepository.findByEmail("admin@admin.com")
                .orElseThrow(() -> new RuntimeException("Admin padrão não encontrado"))
                .getId();

        Usuario usuario = new Usuario();
        usuario.setNick("fav_user");
        usuario.setEmail("fav@user.com");
        usuario.setSenhaHash("123");
        usuarioId = usuarioService.criar(usuario).getId();

        Jogo jogo = new Jogo();
        jogo.setNome("Jogo Favorito");
        jogo.setSlug("jogo-favorito");
        jogo.setCapa("");
        jogo.setCategoria(CategoriaJogo.RPG);
        jogoId = jogoService.criar(jogo, adminId).getId();
    }

    @Test
    void adicionar_comSucesso() {
        JogoFavorito favorito = jogoFavoritoService.adicionar(usuarioId, jogoId);
        assertThat(favorito).isNotNull();
        assertThat(favorito.getJogo().getId()).isEqualTo(jogoId);
        assertThat(favorito.getUsuario().getId()).isEqualTo(usuarioId);
    }

    @Test
    void adicionar_duplicado_lancaExcecao() {
        jogoFavoritoService.adicionar(usuarioId, jogoId);
        assertThatThrownBy(() -> jogoFavoritoService.adicionar(usuarioId, jogoId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Jogo já está nos favoritos");
    }

    @Test
    void listarFavoritosDoUsuario_retornaFavoritos() {
        jogoFavoritoService.adicionar(usuarioId, jogoId);
        List<JogoFavorito> favoritos = jogoFavoritoService.listarFavoritosDoUsuario(usuarioId);
        assertThat(favoritos).hasSize(1);
        assertThat(favoritos.get(0).getJogo().getId()).isEqualTo(jogoId);
    }

    @Test
    void listarFavoritosDoUsuario_semFavoritos_retornaVazio() {
        List<JogoFavorito> favoritos = jogoFavoritoService.listarFavoritosDoUsuario(usuarioId);
        assertThat(favoritos).isEmpty();
    }

    @Test
    void remover_comSucesso() {
        jogoFavoritoService.adicionar(usuarioId, jogoId);
        jogoFavoritoService.remover(usuarioId, jogoId);
        List<JogoFavorito> favoritos = jogoFavoritoService.listarFavoritosDoUsuario(usuarioId);
        assertThat(favoritos).isEmpty();
    }

    @Test
    void remover_naoFavoritado_lancaExcecao() {
        assertThatThrownBy(() -> jogoFavoritoService.remover(usuarioId, jogoId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Favorito não encontrado");
    }
}