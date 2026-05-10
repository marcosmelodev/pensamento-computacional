import { useState } from "react";
import { useLocation } from "wouter";
import { motion, AnimatePresence } from "framer-motion";
import {
  Shield, LogOut, Loader2, Clock, Users, ChevronRight,
  Ticket, ArrowLeft, CheckCircle2, XCircle,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

type Setor = "coordenacao" | "financeiro" | "secretaria";

interface SetorConfig {
  key: Setor;
  label: string;
  prefix: string;
  color: string;
  bg: string;
  border: string;
  descricao: string;
  aguardando: number;
  atualAtendendo: string;
  tempoMedioMin: number;
}

const SETORES: SetorConfig[] = [
  {
    key: "coordenacao",
    label: "Coordenação",
    prefix: "C",
    color: "text-blue-700",
    bg: "bg-blue-50",
    border: "border-blue-200",
    descricao: "Assuntos acadêmicos, aproveitamentos e orientações",
    aguardando: 4,
    atualAtendendo: "C03",
    tempoMedioMin: 12,
  },
  {
    key: "financeiro",
    label: "Financeiro",
    prefix: "F",
    color: "text-emerald-700",
    bg: "bg-emerald-50",
    border: "border-emerald-200",
    descricao: "Boletos, isenções, renegociações e pagamentos",
    aguardando: 2,
    atualAtendendo: "F06",
    tempoMedioMin: 8,
  },
  {
    key: "secretaria",
    label: "Secretaria",
    prefix: "S",
    color: "text-violet-700",
    bg: "bg-violet-50",
    border: "border-violet-200",
    descricao: "Documentos, declarações, matrículas e históricos",
    aguardando: 7,
    atualAtendendo: "S11",
    tempoMedioMin: 6,
  },
];

// Contador de senhas geradas por setor (simulado)
const nextNumbers: Record<Setor, number> = { coordenacao: 8, financeiro: 9, secretaria: 19 };

function pad(n: number) { return String(n).padStart(2, "0"); }

interface Senha {
  codigo: string;
  setor: SetorConfig;
  posicao: number;
  emitidaEm: Date;
}

export default function Dashboard() {
  const [, setLocation] = useLocation();
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [view, setView] = useState<"home" | "senha">("home");
  const [senha, setSenha] = useState<Senha | null>(null);
  const [gerando, setGerando] = useState<Setor | null>(null);

  const handleLogout = () => {
    setIsLoggingOut(true);
    setTimeout(() => setLocation("/"), 600);
  };

  const gerarSenha = (setor: SetorConfig) => {
    setGerando(setor.key);
    setTimeout(() => {
      const numero = nextNumbers[setor.key]++;
      const codigo = `${setor.prefix}${pad(numero)}`;
      setSenha({ codigo, setor, posicao: setor.aguardando + 1, emitidaEm: new Date() });
      setGerando(null);
      setView("senha");
    }, 700);
  };

  const cancelarSenha = () => {
    setSenha(null);
    setView("home");
  };

  const tempoEspera = (s: Senha) =>
    s.posicao * s.setor.tempoMedioMin;

  return (
    <div className="min-h-screen w-full bg-slate-50 flex flex-col">

      <header className="w-full border-b border-slate-200 bg-white px-4 sm:px-6 py-3 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
            <Shield className="w-4 h-4 text-white" />
          </div>
          <span className="font-bold text-slate-900">udfilasystem</span>
        </div>
        <Button variant="ghost" size="sm" onClick={handleLogout} disabled={isLoggingOut} data-testid="button-logout" className="text-slate-500 hover:text-slate-900">
          {isLoggingOut ? <Loader2 className="w-4 h-4 mr-1.5 animate-spin" /> : <LogOut className="w-4 h-4 mr-1.5" />}
          Sair
        </Button>
      </header>

      <main className="flex-1 w-full max-w-lg mx-auto p-4 sm:p-6">
        <AnimatePresence mode="wait">

          {view === "home" && (
            <motion.div key="home" initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -12 }} transition={{ duration: 0.2 }}>
              <div className="mb-6">
                <h1 className="text-2xl font-bold text-slate-900">Bem-vindo!</h1>
                <p className="text-slate-500 text-sm mt-1">Selecione o tipo de atendimento desejado para gerar sua senha.</p>
              </div>

              <div className="flex flex-col gap-3">
                {SETORES.map((setor) => (
                  <button
                    key={setor.key}
                    data-testid={`button-setor-${setor.key}`}
                    onClick={() => gerarSenha(setor)}
                    disabled={gerando !== null}
                    className={`w-full text-left rounded-xl border-2 ${setor.border} ${setor.bg} p-4 transition-all hover:shadow-md hover:scale-[1.01] active:scale-[0.99] disabled:opacity-60 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-primary/30`}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className={`w-11 h-11 rounded-xl ${setor.bg} border ${setor.border} flex items-center justify-center flex-shrink-0`}>
                          {gerando === setor.key
                            ? <Loader2 className={`w-5 h-5 animate-spin ${setor.color}`} />
                            : <span className={`text-xl font-black ${setor.color}`}>{setor.prefix}</span>
                          }
                        </div>
                        <div>
                          <p className={`font-bold text-base ${setor.color}`}>{setor.label}</p>
                          <p className="text-xs text-slate-500 mt-0.5 leading-snug">{setor.descricao}</p>
                        </div>
                      </div>
                      <ChevronRight className="w-5 h-5 text-slate-400 flex-shrink-0 ml-2" />
                    </div>

                    <div className="mt-3 pt-3 border-t border-slate-200/80 flex items-center gap-4 text-xs text-slate-600">
                      <span className="flex items-center gap-1">
                        <Users className="w-3.5 h-3.5" />
                        <strong>{setor.aguardando}</strong> aguardando
                      </span>
                      <span className="flex items-center gap-1">
                        <Ticket className="w-3.5 h-3.5" />
                        Chamando: <strong>{setor.atualAtendendo}</strong>
                      </span>
                      <span className="flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5" />
                        ~{setor.aguardando * setor.tempoMedioMin} min
                      </span>
                    </div>
                  </button>
                ))}
              </div>

              <div className="mt-6 bg-white border border-slate-200 rounded-xl p-4">
                <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide mb-3">Resumo geral</p>
                <div className="grid grid-cols-3 gap-3 text-center">
                  {SETORES.map((s) => (
                    <div key={s.key} className="flex flex-col items-center">
                      <span className={`text-2xl font-black ${s.color}`}>{s.aguardando}</span>
                      <span className="text-xs text-slate-500 mt-0.5">{s.label}</span>
                    </div>
                  ))}
                </div>
              </div>
            </motion.div>
          )}

          {view === "senha" && senha && (
            <motion.div key="senha" initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.96 }} transition={{ duration: 0.25 }}>
              <button onClick={cancelarSenha} className="flex items-center gap-1.5 text-sm text-slate-500 hover:text-slate-800 mb-5 transition-colors" data-testid="button-back-home">
                <ArrowLeft className="w-4 h-4" /> Voltar
              </button>

              <div className={`rounded-2xl border-2 ${senha.setor.border} ${senha.setor.bg} p-6 flex flex-col items-center text-center mb-4`} data-testid="container-senha">
                <div className={`w-14 h-14 rounded-2xl border-2 ${senha.setor.border} bg-white flex items-center justify-center mb-4`}>
                  <span className={`text-2xl font-black ${senha.setor.color}`}>{senha.setor.prefix}</span>
                </div>

                <p className={`text-xs font-semibold uppercase tracking-widest ${senha.setor.color} mb-1`}>{senha.setor.label}</p>
                <p className={`text-6xl font-black tracking-tight ${senha.setor.color} my-2`} data-testid="text-senha-codigo">{senha.codigo}</p>
                <p className="text-slate-500 text-sm">Sua senha de atendimento</p>

                <div className={`w-full mt-5 pt-4 border-t ${senha.setor.border} grid grid-cols-2 gap-3`}>
                  <div className="bg-white/60 rounded-xl p-3">
                    <p className="text-xs text-slate-500 mb-1 flex items-center justify-center gap-1"><Users className="w-3 h-3" /> Na sua frente</p>
                    <p className={`text-2xl font-black ${senha.setor.color}`} data-testid="text-posicao">{senha.posicao - 1}</p>
                    <p className="text-xs text-slate-400">pessoas</p>
                  </div>
                  <div className="bg-white/60 rounded-xl p-3">
                    <p className="text-xs text-slate-500 mb-1 flex items-center justify-center gap-1"><Clock className="w-3 h-3" /> Previsão</p>
                    <p className={`text-2xl font-black ${senha.setor.color}`} data-testid="text-previsao">~{tempoEspera(senha)}</p>
                    <p className="text-xs text-slate-400">minutos</p>
                  </div>
                </div>

                <div className="w-full mt-3 bg-white/60 rounded-xl p-3 flex items-center justify-between">
                  <span className="text-xs text-slate-500 flex items-center gap-1"><Ticket className="w-3 h-3" /> Chamando agora</span>
                  <span className={`text-sm font-bold ${senha.setor.color}`}>{senha.setor.atualAtendendo}</span>
                </div>
              </div>

              <div className="bg-white border border-slate-200 rounded-xl p-4 text-xs text-slate-500 flex items-start gap-2 mb-4">
                <CheckCircle2 className="w-4 h-4 text-green-500 flex-shrink-0 mt-0.5" />
                <p>Fique atento ao painel de chamadas. Sua senha será anunciada quando chegar a sua vez.</p>
              </div>

              <Button
                variant="outline"
                className="w-full border-red-200 text-red-500 hover:bg-red-50 hover:text-red-700"
                onClick={cancelarSenha}
                data-testid="button-cancelar-senha"
              >
                <XCircle className="w-4 h-4 mr-2" />
                Cancelar e sair da fila
              </Button>
            </motion.div>
          )}

        </AnimatePresence>
      </main>
    </div>
  );
}
