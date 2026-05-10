import { useState } from "react";
import { useLocation, Link } from "wouter";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { motion, AnimatePresence } from "framer-motion";
import { Shield, Loader2, User, Mail, Lock, QrCode, SmartphoneNfc, CheckCircle2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";

const registerSchema = z.object({
  name: z.string().min(3, { message: "Nome deve ter pelo menos 3 caracteres" }),
  email: z.string().email({ message: "E-mail inválido" }),
  password: z.string().min(8, { message: "A senha deve ter pelo menos 8 caracteres" })
    .regex(/[A-Z]/, { message: "Deve conter ao menos uma letra maiúscula" })
    .regex(/[0-9]/, { message: "Deve conter ao menos um número" }),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "As senhas não coincidem",
  path: ["confirmPassword"],
});

const totpSchema = z.object({
  code: z.string().length(6, { message: "O código deve ter exatamente 6 dígitos" }).regex(/^\d+$/, { message: "Somente números" }),
});

const steps = [
  { label: "Dados Pessoais" },
  { label: "Configurar 2FA" },
  { label: "Confirmar Código" },
];

export default function Register() {
  const [, setLocation] = useLocation();
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [isLoading, setIsLoading] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState("");

  const form = useForm<z.infer<typeof registerSchema>>({
    resolver: zodResolver(registerSchema),
    defaultValues: { name: "", email: "", password: "", confirmPassword: "" },
  });

  const totpForm = useForm<z.infer<typeof totpSchema>>({
    resolver: zodResolver(totpSchema),
    defaultValues: { code: "" },
  });

  const onSubmitStep1 = async (values: z.infer<typeof registerSchema>) => {
    setIsLoading(true);
    setTimeout(() => {
      setRegisteredEmail(values.email);
      setIsLoading(false);
      setStep(2);
    }, 800);
  };

  const onGoToStep3 = () => {
    setStep(3);
  };

  const onConfirmTotp = async (values: z.infer<typeof totpSchema>) => {
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      setLocation("/login");
    }, 800);
  };

  return (
    <div className="min-h-screen w-full bg-slate-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md">

        <div className="mb-6 text-center flex flex-col items-center">
          <Link href="/" className="inline-flex items-center justify-center w-12 h-12 bg-primary text-white rounded-xl shadow-sm mb-4" data-testid="link-home-logo">
            <Shield className="w-6 h-6" />
          </Link>
          <h1 className="text-2xl font-bold text-slate-900">Cadastro de Usuário</h1>
        </div>

        <div className="flex items-center gap-2 mb-6">
          {steps.map((s, i) => {
            const n = i + 1;
            const active   = step === n;
            const done     = step > n;
            return (
              <div key={n} className="flex items-center flex-1">
                <div className={`flex items-center justify-center w-7 h-7 rounded-full text-xs font-bold flex-shrink-0 border-2 transition-colors
                  ${done  ? "bg-primary border-primary text-white"
                  : active ? "bg-white border-primary text-primary"
                  :          "bg-white border-slate-200 text-slate-400"}`}>
                  {done ? <CheckCircle2 className="w-4 h-4" /> : n}
                </div>
                <span className={`ml-1.5 text-xs font-medium truncate ${active ? "text-primary" : done ? "text-slate-600" : "text-slate-400"}`}>
                  {s.label}
                </span>
                {i < steps.length - 1 && <div className={`flex-1 h-px mx-2 ${done ? "bg-primary" : "bg-slate-200"}`} />}
              </div>
            );
          })}
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 sm:p-8 relative overflow-hidden">
          <AnimatePresence mode="wait">

            {step === 1 && (
              <motion.div key="step1" initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} transition={{ duration: 0.2 }}>
                <Form {...form}>
                  <form onSubmit={form.handleSubmit(onSubmitStep1)} className="space-y-4">
                    <FormField control={form.control} name="name" render={({ field }) => (
                      <FormItem>
                        <FormLabel>Nome completo</FormLabel>
                        <FormControl>
                          <div className="relative">
                            <User className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                            <Input placeholder="Seu nome completo" className="pl-10" {...field} data-testid="input-name" />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )} />
                    <FormField control={form.control} name="email" render={({ field }) => (
                      <FormItem>
                        <FormLabel>E-mail institucional</FormLabel>
                        <FormControl>
                          <div className="relative">
                            <Mail className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                            <Input placeholder="usuario@instituicao.edu.br" className="pl-10" {...field} data-testid="input-email" />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )} />
                    <FormField control={form.control} name="password" render={({ field }) => (
                      <FormItem>
                        <FormLabel>Senha</FormLabel>
                        <FormControl>
                          <div className="relative">
                            <Lock className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                            <Input type="password" placeholder="Mín. 8 caracteres, maiúscula e número" className="pl-10" {...field} data-testid="input-password" />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )} />
                    <FormField control={form.control} name="confirmPassword" render={({ field }) => (
                      <FormItem>
                        <FormLabel>Confirmar senha</FormLabel>
                        <FormControl>
                          <div className="relative">
                            <Lock className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                            <Input type="password" placeholder="Repita a senha" className="pl-10" {...field} data-testid="input-confirm-password" />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )} />
                    <div className="pt-2">
                      <Button type="submit" className="w-full h-11" disabled={isLoading} data-testid="button-submit-register">
                        {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : "Continuar"}
                      </Button>
                    </div>
                  </form>
                </Form>
              </motion.div>
            )}

            {step === 2 && (
              <motion.div key="step2" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 20 }} transition={{ duration: 0.2 }} className="flex flex-col items-center">
                <div className="w-12 h-12 bg-blue-50 rounded-full flex items-center justify-center mb-4">
                  <QrCode className="w-6 h-6 text-primary" />
                </div>
                <h2 className="text-lg font-semibold text-slate-900 mb-1">Configurar Microsoft Authenticator</h2>
                <p className="text-sm text-slate-500 text-center mb-5 px-2">
                  Abra o aplicativo Microsoft Authenticator, toque em <strong>+ Adicionar conta</strong> e escaneie o QR Code abaixo.
                </p>

                <div className="bg-white p-4 rounded-xl border-2 border-slate-200 shadow-sm mb-5" data-testid="container-qrcode">
                  <svg width="180" height="180" viewBox="0 0 180 180" xmlns="http://www.w3.org/2000/svg">
                    <rect width="180" height="180" fill="#ffffff"/>
                    <rect x="20" y="20" width="10" height="10" fill="#000"/><rect x="30" y="20" width="10" height="10" fill="#000"/><rect x="40" y="20" width="10" height="10" fill="#000"/><rect x="50" y="20" width="10" height="10" fill="#000"/><rect x="60" y="20" width="10" height="10" fill="#000"/>
                    <rect x="20" y="30" width="10" height="10" fill="#000"/><rect x="60" y="30" width="10" height="10" fill="#000"/>
                    <rect x="20" y="40" width="10" height="10" fill="#000"/><rect x="40" y="40" width="10" height="10" fill="#000"/><rect x="60" y="40" width="10" height="10" fill="#000"/>
                    <rect x="20" y="50" width="10" height="10" fill="#000"/><rect x="40" y="50" width="10" height="10" fill="#000"/><rect x="60" y="50" width="10" height="10" fill="#000"/>
                    <rect x="20" y="60" width="10" height="10" fill="#000"/><rect x="30" y="60" width="10" height="10" fill="#000"/><rect x="40" y="60" width="10" height="10" fill="#000"/><rect x="50" y="60" width="10" height="10" fill="#000"/><rect x="60" y="60" width="10" height="10" fill="#000"/>
                    <rect x="80" y="20" width="10" height="10" fill="#000"/><rect x="100" y="20" width="10" height="10" fill="#000"/>
                    <rect x="70" y="30" width="10" height="10" fill="#000"/><rect x="90" y="30" width="10" height="10" fill="#000"/>
                    <rect x="80" y="40" width="10" height="10" fill="#000"/><rect x="100" y="40" width="10" height="10" fill="#000"/>
                    <rect x="70" y="50" width="10" height="10" fill="#000"/><rect x="80" y="50" width="10" height="10" fill="#000"/><rect x="100" y="50" width="10" height="10" fill="#000"/>
                    <rect x="90" y="60" width="10" height="10" fill="#000"/><rect x="100" y="60" width="10" height="10" fill="#000"/>
                    <rect x="110" y="20" width="10" height="10" fill="#000"/><rect x="120" y="20" width="10" height="10" fill="#000"/><rect x="130" y="20" width="10" height="10" fill="#000"/><rect x="140" y="20" width="10" height="10" fill="#000"/><rect x="150" y="20" width="10" height="10" fill="#000"/>
                    <rect x="110" y="30" width="10" height="10" fill="#000"/><rect x="150" y="30" width="10" height="10" fill="#000"/>
                    <rect x="110" y="40" width="10" height="10" fill="#000"/><rect x="130" y="40" width="10" height="10" fill="#000"/><rect x="150" y="40" width="10" height="10" fill="#000"/>
                    <rect x="110" y="50" width="10" height="10" fill="#000"/><rect x="130" y="50" width="10" height="10" fill="#000"/><rect x="150" y="50" width="10" height="10" fill="#000"/>
                    <rect x="110" y="60" width="10" height="10" fill="#000"/><rect x="120" y="60" width="10" height="10" fill="#000"/><rect x="130" y="60" width="10" height="10" fill="#000"/><rect x="140" y="60" width="10" height="10" fill="#000"/><rect x="150" y="60" width="10" height="10" fill="#000"/>
                    <rect x="20" y="110" width="10" height="10" fill="#000"/><rect x="30" y="110" width="10" height="10" fill="#000"/><rect x="40" y="110" width="10" height="10" fill="#000"/><rect x="50" y="110" width="10" height="10" fill="#000"/><rect x="60" y="110" width="10" height="10" fill="#000"/>
                    <rect x="20" y="120" width="10" height="10" fill="#000"/><rect x="60" y="120" width="10" height="10" fill="#000"/>
                    <rect x="20" y="130" width="10" height="10" fill="#000"/><rect x="40" y="130" width="10" height="10" fill="#000"/><rect x="60" y="130" width="10" height="10" fill="#000"/>
                    <rect x="20" y="140" width="10" height="10" fill="#000"/><rect x="40" y="140" width="10" height="10" fill="#000"/>
                    <rect x="20" y="150" width="10" height="10" fill="#000"/><rect x="30" y="150" width="10" height="10" fill="#000"/><rect x="40" y="150" width="10" height="10" fill="#000"/><rect x="50" y="150" width="10" height="10" fill="#000"/><rect x="60" y="150" width="10" height="10" fill="#000"/>
                    <rect x="80" y="80" width="10" height="10" fill="#000"/><rect x="100" y="80" width="10" height="10" fill="#000"/><rect x="120" y="80" width="10" height="10" fill="#000"/>
                    <rect x="70" y="90" width="10" height="10" fill="#000"/><rect x="90" y="90" width="10" height="10" fill="#000"/><rect x="110" y="90" width="10" height="10" fill="#000"/><rect x="130" y="90" width="10" height="10" fill="#000"/>
                    <rect x="80" y="100" width="10" height="10" fill="#000"/><rect x="100" y="100" width="10" height="10" fill="#000"/><rect x="140" y="100" width="10" height="10" fill="#000"/>
                    <rect x="70" y="110" width="10" height="10" fill="#000"/><rect x="90" y="110" width="10" height="10" fill="#000"/><rect x="120" y="110" width="10" height="10" fill="#000"/>
                    <rect x="80" y="120" width="10" height="10" fill="#000"/><rect x="110" y="120" width="10" height="10" fill="#000"/><rect x="130" y="120" width="10" height="10" fill="#000"/><rect x="150" y="120" width="10" height="10" fill="#000"/>
                    <rect x="70" y="130" width="10" height="10" fill="#000"/><rect x="100" y="130" width="10" height="10" fill="#000"/><rect x="140" y="130" width="10" height="10" fill="#000"/>
                    <rect x="80" y="140" width="10" height="10" fill="#000"/><rect x="90" y="140" width="10" height="10" fill="#000"/><rect x="120" y="140" width="10" height="10" fill="#000"/>
                    <rect x="70" y="150" width="10" height="10" fill="#000"/><rect x="110" y="150" width="10" height="10" fill="#000"/><rect x="130" y="150" width="10" height="10" fill="#000"/><rect x="150" y="150" width="10" height="10" fill="#000"/>
                  </svg>
                </div>

                <div className="w-full bg-blue-50 border border-blue-100 rounded-lg p-3 mb-5 flex items-start gap-3">
                  <SmartphoneNfc className="w-5 h-5 text-blue-500 flex-shrink-0 mt-0.5" />
                  <p className="text-xs text-blue-700">
                    Após escanear, o aplicativo irá gerar um código de 6 dígitos renovado a cada 30 segundos. Use-o sempre que fizer login.
                  </p>
                </div>

                <Button type="button" className="w-full h-11" onClick={onGoToStep3} data-testid="button-next-step3">
                  Já escaneei — Confirmar código
                </Button>
              </motion.div>
            )}

            {step === 3 && (
              <motion.div key="step3" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 20 }} transition={{ duration: 0.2 }} className="flex flex-col items-center">
                <div className="w-12 h-12 bg-green-50 rounded-full flex items-center justify-center mb-4">
                  <SmartphoneNfc className="w-6 h-6 text-green-600" />
                </div>
                <h2 className="text-lg font-semibold text-slate-900 mb-1">Confirmar Autenticador</h2>
                <p className="text-sm text-slate-500 text-center mb-6 px-2">
                  Digite o código de 6 dígitos exibido agora no <strong>Microsoft Authenticator</strong> para confirmar a configuração.
                </p>

                <Form {...totpForm}>
                  <form onSubmit={totpForm.handleSubmit(onConfirmTotp)} className="w-full space-y-5">
                    <FormField control={totpForm.control} name="code" render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-center block">Código de verificação</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="000000"
                            maxLength={6}
                            className="text-center text-3xl tracking-[0.5em] font-mono h-14 border-2 border-slate-200 focus:border-primary"
                            {...field}
                            data-testid="input-totp-confirm"
                          />
                        </FormControl>
                        <FormMessage className="text-center" />
                      </FormItem>
                    )} />

                    <div className="flex flex-col gap-2 pt-1">
                      <Button type="submit" className="w-full h-11 bg-green-600 hover:bg-green-700" disabled={isLoading} data-testid="button-confirm-totp">
                        {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : "Confirmar e Finalizar Cadastro"}
                      </Button>
                      <Button type="button" variant="ghost" size="sm" className="text-slate-500" onClick={() => setStep(2)} data-testid="button-back-qr">
                        Voltar ao QR Code
                      </Button>
                    </div>
                  </form>
                </Form>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {step === 1 && (
          <div className="mt-6 text-center">
            <p className="text-sm text-slate-500">
              Já tenho uma conta?{" "}
              <Link href="/login" className="font-medium text-primary hover:underline" data-testid="link-login">
                Entrar
              </Link>
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
