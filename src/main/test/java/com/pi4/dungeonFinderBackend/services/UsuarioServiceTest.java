package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
import com.pi4.dungeonFinderBackend.datasource.repositories.UsuarioRepository;
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
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setNick("joaosilva");
        usuario.setEmail("joao@email.com");
        usuario.setSenhaHash("123456");
        usuario.setBio("Olá, mundo!");
        usuario.setIsAdmin(false);
    }

    @Test
    void criarUsuario_comSucesso() {
        Usuario salvo = usuarioService.criar(usuario);
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNick()).isEqualTo("joaosilva");
        assertThat(salvo.getEmail()).isEqualTo("joao@email.com");
        assertThat(salvo.getIsAdmin()).isFalse();
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    @Test
    void criarUsuario_emailDuplicado_lancaExcecao() {
        usuarioService.criar(usuario);
        Usuario outro = new Usuario();
        outro.setNick("outro");
        outro.setEmail("joao@email.com");
        outro.setSenhaHash("123");

        assertThatThrownBy(() -> usuarioService.criar(outro))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void buscarPorId_comSucesso() {
        Usuario salvo = usuarioService.criar(usuario);
        Usuario encontrado = usuarioService.buscarPorId(salvo.getId());
        assertThat(encontrado.getNick()).isEqualTo("joaosilva");
    }

    @Test
    void buscarPorId_naoEncontrado_lancaExcecao() {
        UUID idAleatorio = UUID.randomUUID();
        assertThatThrownBy(() -> usuarioService.buscarPorId(idAleatorio))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void atualizarUsuario_comSucesso() {
        Usuario salvo = usuarioService.criar(usuario);
        Usuario novosDados = new Usuario();
        novosDados.setNick("maria");
        novosDados.setEmail("maria@email.com");
        novosDados.setBio("Nova bio");
        novosDados.setSenhaHash("novaSenha");

        Usuario atualizado = usuarioService.atualizar(salvo.getId(), novosDados);
        assertThat(atualizado.getNick()).isEqualTo("maria");
        assertThat(atualizado.getEmail()).isEqualTo("maria@email.com");
        assertThat(atualizado.getBio()).isEqualTo("Nova bio");
        assertThat(atualizado.getSenhaHash()).isEqualTo("novaSenha");
    }

    @Test
    void deletarUsuario_comSucesso() {
        Usuario salvo = usuarioService.criar(usuario);
        usuarioService.deletar(salvo.getId());
        assertThat(usuarioRepository.findById(salvo.getId())).isEmpty();
    }

    @Test
    void tornarAdmin_comSucesso() {
        Usuario salvo = usuarioService.criar(usuario);
        Usuario admin = usuarioService.tornarAdmin(salvo.getId());
        assertThat(admin.getIsAdmin()).isTrue();
    }

    @Test
    void listarTodos_contemNovoUsuario() {
        usuarioService.criar(usuario);
        List<Usuario> lista = usuarioService.listarTodos();
        assertThat(lista).anyMatch(u -> u.getEmail().equals("joao@email.com"));
    }
}