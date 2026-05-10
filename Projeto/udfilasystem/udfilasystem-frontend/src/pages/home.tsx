import { Link } from "wouter";
import { ShieldCheck, LogIn, UserPlus } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <div className="min-h-screen w-full bg-slate-50 dark:bg-slate-950 flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Subtle background pattern */}
      <div 
        className="absolute inset-0 pointer-events-none opacity-[0.03] dark:opacity-[0.05]"
        style={{
          backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23000000' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
        }}
      />

      <div className="w-full max-w-md bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 p-8 relative z-10 flex flex-col items-center text-center">
        <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mb-6">
          <ShieldCheck className="w-8 h-8 text-primary" />
        </div>
        
        <h1 className="text-3xl font-bold text-slate-900 dark:text-slate-100 tracking-tight mb-2">
          udfilasystem
        </h1>
        <p className="text-slate-500 dark:text-slate-400 mb-8 max-w-xs leading-relaxed">
          Sistema de Gerenciamento de Filas — acesse sua conta para continuar.
        </p>

        <div className="w-full flex flex-col gap-3">
          <Link href="/login" className="w-full">
            <Button className="w-full h-12 text-base font-medium" data-testid="link-login">
              <LogIn className="w-4 h-4 mr-2" />
              Entrar
            </Button>
          </Link>
          
          <Link href="/cadastrar" className="w-full">
            <Button variant="outline" className="w-full h-12 text-base font-medium" data-testid="link-register">
              <UserPlus className="w-4 h-4 mr-2" />
              Cadastrar
            </Button>
          </Link>
        </div>

        <div className="mt-8 pt-6 border-t border-slate-100 dark:border-slate-800 w-full">
          <p className="text-xs text-slate-400">
            &copy; {new Date().getFullYear()} udfilasystem. Acesso restrito.
          </p>
        </div>
      </div>
    </div>
  );
}
