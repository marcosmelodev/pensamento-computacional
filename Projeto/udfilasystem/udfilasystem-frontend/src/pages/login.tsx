import { useState } from "react";
import { useLocation, Link } from "wouter";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { motion, AnimatePresence } from "framer-motion";
import { Shield, ArrowLeft, Loader2, KeyRound, Mail, Lock } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";

const credentialsSchema = z.object({
  email: z.string().email({ message: "E-mail inválido" }),
  password: z.string().min(1, { message: "Senha é obrigatória" }),
});

const totpSchema = z.object({
  code: z.string().length(6, { message: "O código deve ter 6 dígitos" }),
});

export default function Login() {
  const [, setLocation] = useLocation();
  const [step, setStep] = useState<1 | 2>(1);
  const [isLoading, setIsLoading] = useState(false);
  const [loginError, setLoginError] = useState<string | null>(null);

  const credsForm = useForm<z.infer<typeof credentialsSchema>>({
    resolver: zodResolver(credentialsSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const totpForm = useForm<z.infer<typeof totpSchema>>({
    resolver: zodResolver(totpSchema),
    defaultValues: {
      code: "",
    },
  });

  const onCredsSubmit = async (values: z.infer<typeof credentialsSchema>) => {
    setIsLoading(true);
    setLoginError(null);
    
    // Simulate API call
    setTimeout(() => {
      setIsLoading(false);
      // For demo, accept any valid email
      setStep(2);
    }, 800);
  };

  const onTotpSubmit = async (values: z.infer<typeof totpSchema>) => {
    setIsLoading(true);
    setLoginError(null);

    // Simulate API call
    setTimeout(() => {
      setIsLoading(false);
      setLocation("/dashboard");
    }, 800);
  };

  return (
    <div className="min-h-screen w-full bg-slate-50 dark:bg-slate-950 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center flex flex-col items-center">
          <Link href="/" className="inline-flex items-center justify-center w-12 h-12 bg-primary text-white rounded-xl shadow-sm mb-4" data-testid="link-home-logo">
            <Shield className="w-6 h-6" />
          </Link>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100">Acesso ao Sistema</h1>
          <p className="text-slate-500 text-sm mt-1">
            Passo {step} de 2: {step === 1 ? "Credenciais" : "Autenticação"}
          </p>
        </div>

        <div className="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 p-6 sm:p-8 relative overflow-hidden">
          <AnimatePresence mode="wait">
            {step === 1 && (
              <motion.div
                key="step1"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.2 }}
              >
                <Form {...credsForm}>
                  <form onSubmit={credsForm.handleSubmit(onCredsSubmit)} className="space-y-5">
                    <FormField
                      control={credsForm.control}
                      name="email"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>E-mail corporativo</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <Mail className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                              <Input placeholder="usuario@instituicao.edu.br" className="pl-10" {...field} data-testid="input-email" />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={credsForm.control}
                      name="password"
                      render={({ field }) => (
                        <FormItem>
                          <div className="flex items-center justify-between">
                            <FormLabel>Senha</FormLabel>
                            <button type="button" className="text-xs text-primary hover:underline" data-testid="link-forgot-password">
                              Esqueceu a senha?
                            </button>
                          </div>
                          <FormControl>
                            <div className="relative">
                              <Lock className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                              <Input type="password" placeholder="••••••••" className="pl-10" {...field} data-testid="input-password" />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    {loginError && (
                      <div className="p-3 text-sm text-red-600 bg-red-50 dark:bg-red-900/20 rounded-md border border-red-100 dark:border-red-900/30">
                        {loginError}
                      </div>
                    )}

                    <div className="pt-2">
                      <Button type="submit" className="w-full h-11" disabled={isLoading} data-testid="button-submit-creds">
                        {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : "Continuar"}
                      </Button>
                    </div>
                  </form>
                </Form>
              </motion.div>
            )}

            {step === 2 && (
              <motion.div
                key="step2"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 20 }}
                transition={{ duration: 0.2 }}
                className="flex flex-col items-center"
              >
                <div className="w-12 h-12 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mb-6">
                  <KeyRound className="w-6 h-6 text-primary" />
                </div>
                <h2 className="text-xl font-semibold mb-2">Autenticação em duas etapas</h2>
                <p className="text-sm text-slate-500 text-center mb-6">
                  Digite o código de 6 dígitos gerado pelo seu aplicativo Microsoft Authenticator.
                </p>

                <Form {...totpForm}>
                  <form onSubmit={totpForm.handleSubmit(onTotpSubmit)} className="w-full flex flex-col items-center">
                    <FormField
                      control={totpForm.control}
                      name="code"
                      render={({ field }) => (
                        <FormItem className="flex flex-col items-center">
                          <FormControl>
                            <InputOTP maxLength={6} {...field} data-testid="input-totp">
                              <InputOTPGroup>
                                <InputOTPSlot index={0} />
                                <InputOTPSlot index={1} />
                                <InputOTPSlot index={2} />
                                <InputOTPSlot index={3} />
                                <InputOTPSlot index={4} />
                                <InputOTPSlot index={5} />
                              </InputOTPGroup>
                            </InputOTP>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="w-full flex gap-3 mt-8">
                      <Button
                        type="button"
                        variant="outline"
                        className="h-11 px-4"
                        onClick={() => setStep(1)}
                        disabled={isLoading}
                        data-testid="button-back"
                      >
                        <ArrowLeft className="w-4 h-4" />
                      </Button>
                      <Button type="submit" className="flex-1 h-11" disabled={isLoading} data-testid="button-submit-totp">
                        {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : "Verificar"}
                      </Button>
                    </div>
                  </form>
                </Form>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        <div className="mt-8 text-center">
          <p className="text-sm text-slate-500">
            Não possui uma conta?{" "}
            <Link href="/cadastrar" className="font-medium text-primary hover:underline" data-testid="link-register">
              Solicitar acesso
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
