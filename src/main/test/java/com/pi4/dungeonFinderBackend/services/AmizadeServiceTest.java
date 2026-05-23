package com.pi4.dungeonFinderBackend.services;

import com.pi4.dungeonFinderBackend.domain.entities.Amizade;
import com.pi4.dungeonFinderBackend.domain.entities.StatusAmizade;
import com.pi4.dungeonFinderBackend.domain.entities.Usuario;
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
class AmizadeServiceTest {

    @Autowired
    private AmizadeService amizadeService;

    @Autowired
    private UsuarioService usuarioService;

    private UUID usuario1Id;
    private UUID usuario2Id;

    @BeforeEach
    void setUp() {
        Usuario u1 = new Usuario();
        u1.setNick("amigo1");
        u1.setEmail("amigo1@test.com");
        u1.setSenhaHash("123");
        usuario1Id = usuarioService.criar(u1).getId();

        Usuario u2 = new Usuario();
        u2.setNick("amigo2");
        u2.setEmail("amigo2@test.com");
        u2.setSenhaHash("123");
        usuario2Id = usuarioService.criar(u2).getId();
    }

    @Test
    void solicitarAmizade_comSucesso() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        assertThat(amizade.getId()).isNotNull();
        assertThat(amizade.getStatus()).isEqualTo(StatusAmizade.PENDENTE);
        assertThat(amizade.getSolicitante().getId()).isEqualTo(usuario1Id);
        assertThat(amizade.getDestinatario().getId()).isEqualTo(usuario2Id);
    }

    @Test
    void solicitarAmizade_paraSiMesmo_lancaExcecao() {
        assertThatThrownBy(() -> amizadeService.solicitar(usuario1Id, usuario1Id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST");
    }

    @Test
    void solicitarAmizade_duplicada_lancaExcecao() {
        amizadeService.solicitar(usuario1Id, usuario2Id);
        assertThatThrownBy(() -> amizadeService.solicitar(usuario1Id, usuario2Id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    @Test
    void listarPendentes_retornaSolicitacoesRecebidas() {
        amizadeService.solicitar(usuario1Id, usuario2Id);
        List<Amizade> pendentes = amizadeService.listarPendentes(usuario2Id);
        assertThat(pendentes).hasSize(1);
        assertThat(pendentes.get(0).getSolicitante().getId()).isEqualTo(usuario1Id);
    }

    @Test
    void listarEnviadas_retornaSolicitacoesEnviadas() {
        amizadeService.solicitar(usuario1Id, usuario2Id);
        List<Amizade> enviadas = amizadeService.listarEnviadas(usuario1Id);
        assertThat(enviadas).hasSize(1);
        assertThat(enviadas.get(0).getDestinatario().getId()).isEqualTo(usuario2Id);
    }

    @Test
    void responder_aceitar_comSucesso() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        Amizade aceita = amizadeService.responder(amizade.getId(), StatusAmizade.ACEITO, usuario2Id);
        assertThat(aceita.getStatus()).isEqualTo(StatusAmizade.ACEITO);
    }

    @Test
    void responder_recusar_comSucesso() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        Amizade recusada = amizadeService.responder(amizade.getId(), StatusAmizade.RECUSADO, usuario2Id);
        assertThat(recusada.getStatus()).isEqualTo(StatusAmizade.RECUSADO);
    }

    @Test
    void responder_comUsuarioNaoDestinatario_lancaExcecao() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        assertThatThrownBy(() -> amizadeService.responder(amizade.getId(), StatusAmizade.ACEITO, usuario1Id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    @Test
    void listarAmizades_retornaApenasAceitas() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        amizadeService.responder(amizade.getId(), StatusAmizade.ACEITO, usuario2Id);
        List<Amizade> amizades = amizadeService.listarAmizades(usuario1Id);
        assertThat(amizades).hasSize(1);
        assertThat(amizades.get(0).getStatus()).isEqualTo(StatusAmizade.ACEITO);
    }

    @Test
    void deletarAmizade_peloSolicitante_comSucesso() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        amizadeService.deletar(amizade.getId(), usuario1Id);
        assertThatThrownBy(() -> amizadeService.buscarPorId(amizade.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deletarAmizade_peloDestinatario_comSucesso() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        amizadeService.deletar(amizade.getId(), usuario2Id);
        assertThatThrownBy(() -> amizadeService.buscarPorId(amizade.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deletarAmizade_porUsuarioNaoEnvolvido_lancaExcecao() {
        Usuario u3 = new Usuario();
        u3.setNick("outro");
        u3.setEmail("outro@test.com");
        u3.setSenhaHash("123");
        UUID outroId = usuarioService.criar(u3).getId();

        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        assertThatThrownBy(() -> amizadeService.deletar(amizade.getId(), outroId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    @Test
    void buscarPorId_comSucesso() {
        Amizade amizade = amizadeService.solicitar(usuario1Id, usuario2Id);
        Amizade encontrada = amizadeService.buscarPorId(amizade.getId());
        assertThat(encontrada.getId()).isEqualTo(amizade.getId());
    }

    @Test
    void buscarPorId_inexistente_lancaExcecao() {
        assertThatThrownBy(() -> amizadeService.buscarPorId(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Amizade não encontrada");
    }
}