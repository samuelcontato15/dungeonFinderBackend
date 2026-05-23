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
class NotificacaoServiceTest {

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UUID usuarioId;
    private UUID outroUsuarioId;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setNick("notif_user");
        usuario.setEmail("notif@user.com");
        usuario.setSenhaHash("123");
        usuarioId = usuarioService.criar(usuario).getId();

        Usuario outro = new Usuario();
        outro.setNick("outro_notif");
        outro.setEmail("outro@notif.com");
        outro.setSenhaHash("123");
        outroUsuarioId = usuarioService.criar(outro).getId();
    }

    private Notificacao criarNotificacao(UUID destinatario) {
        return notificacaoService.criar(
                destinatario,
                TipoNotificacao.SISTEMA,
                "Mensagem de teste",
                UUID.randomUUID(),
                "SISTEMA"
        );
    }

    @Test
    void criar_comSucesso() {
        Notificacao notif = criarNotificacao(usuarioId);
        assertThat(notif.getId()).isNotNull();
        assertThat(notif.getMensagem()).isEqualTo("Mensagem de teste");
        assertThat(notif.getLida()).isFalse();
    }

    @Test
    void listarPorUsuario_retornaNotificacoesDoUsuario() {
        criarNotificacao(usuarioId);
        criarNotificacao(usuarioId);
        criarNotificacao(outroUsuarioId); // não deve aparecer

        List<Notificacao> lista = notificacaoService.listarPorUsuario(usuarioId);
        assertThat(lista).hasSize(2);
        assertThat(lista).allMatch(n -> n.getUsuario().getId().equals(usuarioId));
    }

    @Test
    void listarNaoLidas_retornaApenasNaoLidas() {
        Notificacao n1 = criarNotificacao(usuarioId);
        criarNotificacao(usuarioId);
        notificacaoService.marcarComoLida(n1.getId(), usuarioId);

        List<Notificacao> naoLidas = notificacaoService.listarNaoLidas(usuarioId);
        assertThat(naoLidas).hasSize(1);
        assertThat(naoLidas.get(0).getLida()).isFalse();
    }

    @Test
    void contarNaoLidas_retornaContagem() {
        criarNotificacao(usuarioId);
        criarNotificacao(usuarioId);

        long count = notificacaoService.contarNaoLidas(usuarioId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void marcarComoLida_comSucesso() {
        Notificacao notif = criarNotificacao(usuarioId);
        Notificacao lida = notificacaoService.marcarComoLida(notif.getId(), usuarioId);
        assertThat(lida.getLida()).isTrue();
    }

    @Test
    void marcarComoLida_porOutroUsuario_lancaExcecao() {
        Notificacao notif = criarNotificacao(usuarioId);
        assertThatThrownBy(() -> notificacaoService.marcarComoLida(notif.getId(), outroUsuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }

    @Test
    void marcarTodasComoLidas_comSucesso() {
        criarNotificacao(usuarioId);
        criarNotificacao(usuarioId);

        notificacaoService.marcarTodasComoLidas(usuarioId);

        assertThat(notificacaoService.contarNaoLidas(usuarioId)).isZero();
    }

    @Test
    void deletar_comSucesso() {
        Notificacao notif = criarNotificacao(usuarioId);
        notificacaoService.deletar(notif.getId(), usuarioId);

        List<Notificacao> lista = notificacaoService.listarPorUsuario(usuarioId);
        assertThat(lista).isEmpty();
    }

    @Test
    void deletar_porOutroUsuario_lancaExcecao() {
        Notificacao notif = criarNotificacao(usuarioId);
        assertThatThrownBy(() -> notificacaoService.deletar(notif.getId(), outroUsuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }

    @Test
    void deletar_notificacaoInexistente_lancaExcecao() {
        assertThatThrownBy(() -> notificacaoService.deletar(UUID.randomUUID(), usuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Notificação não encontrada");
    }
}