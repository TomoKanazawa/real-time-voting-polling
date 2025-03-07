import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Distributed Real-Time Voting System",
  description: "A scalable, fault-tolerant, real-time voting and polling system",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <div className="min-h-screen bg-gray-100">
          <header className="bg-white shadow">
            <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
              <h1 className="text-3xl font-bold text-gray-900">
                Distributed Real-Time Voting System
              </h1>
            </div>
          </header>
          <main>
            <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
              {children}
            </div>
          </main>
          <footer className="bg-white shadow mt-auto py-4">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-gray-500">
              &copy; {new Date().getFullYear()} Distributed Real-Time Voting System
            </div>
          </footer>
        </div>
      </body>
    </html>
  );
}
