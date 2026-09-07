import type { Metadata } from "next";
import "./globals.css";
import { PhoneChatMockup } from "@/components/PhoneChatMockup";
import { AuthProvider } from "@/contexts/AuthContext";

export const metadata: Metadata = {
  title: "Heartmate - Find someone who feels like home",
  description: "8-bit Pixel Matrimonial & Matchmaking Platform",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <head>
        <link
          href="https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap"
          rel="stylesheet"
        />
      </head>
      <body style={{ margin: 0 }}>
        <AuthProvider>
          {children}
          <PhoneChatMockup />
        </AuthProvider>
      </body>
    </html>
  );
}
