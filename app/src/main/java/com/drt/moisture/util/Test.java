package com.drt.moisture.util;


public class Test {

    // 定义函数L_Z
    // 定义求解一元三次方程最小根函数
    public static double L_Z(double a, double b, double c, double d) {
        // MIN
        double BigA, BigB, BigC, Y1, Y2, K, x = 0, x1, x2, x3, T, theta;
        BigA = Math.pow(b, 2) - 3 * a * c;
        BigB = b * c - 9 * a * d;
        BigC = Math.pow(c, 2) - 3 * b * d;
        if (BigA == 0 && BigB == 0) {
            x = -3 * d / c;
        } else if (Math.pow(BigB, 2) - 4 * BigA * BigC > 0) {
            Y1 = BigA * b + 3 * a * ((-BigB + Math.pow((Math.pow(BigB, 2) - 4 * BigA * BigC), 0.5)) / 2);
            Y2 = BigA * b + 3 * a * ((-BigB - Math.pow((Math.pow(BigB, 2) - 4 * BigA * BigC), 0.5)) / 2);
            if (Y1 > 0 && Y2 > 0) {
                x = (-b - (Math.pow(Y1, (1 / 3.0)) + Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            } else if (Y1 > 0 && Y2 < 0) {
                Y2 = Math.abs(Y2);
                x = (-b - (Math.pow(Y1, (1 / 3.0)) - Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            } else if (Y1 < 0 && Y2 > 0) {
                Y1 = Math.abs(Y1);
                x = (-b - (-Math.pow(Y1, (1 / 3.0)) + Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            } else if (Y1 < 0 && Y2 < 0) {
                Y2 = Math.abs(Y2);
                Y1 = Math.abs(Y1);
                x = (-b + (Math.pow(Y1, (1 / 3.0)) + Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            }
        } else if (Math.pow(BigB, 2) - 4 * BigA * BigC == 0 && BigA != 0) {
            K = BigB / BigA;
            x1 = -b / a + K;
            x2 = -K / 2;
            x3 = -K / 2;
            if (x1 >= x2) {
                x = x2;
            } else if (x2 >= x1) {
                x = x1;
            }
        } else if (Math.pow(BigB, 2) - 4 * BigA * BigC < 0 && BigA > 0) {
            T = (2 * BigA * b - 3 * a * BigB) / (2 * Math.pow((Math.pow(BigA, 3)), 0.5));
            System.out.println(T);
            theta = Math.acos(T);
            x1 = (-b - 2 * Math.pow(BigA, 0.5) * Math.cos(theta / 3)) / (3 * a);
            x2 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3) + Math.pow(3, 0.5) * Math.sin(theta / 3))) / (3 * a);
            x3 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3) - Math.pow(3, 0.5) * Math.sin(theta / 3))) / (3 * a);
            if (x1 >= x2) {
                x = x2;
            } else if (x2 > x1) {
                x = x1;
            }
            if (x >= x3) {
                x = x3;
            }
        }
        return x;

    }

    // 定义求解一元三次方程最大根函数
    public static double V_Z(double a, double b, double c, double d) {
        // MAX
        double BigA, BigB, BigC, Y1, Y2, K, x = 0, x1, x2, x3, T, theta;
        BigA = Math.pow(b, 2) - 3 * a * c;
        BigB = b * c - 9 * a * d;
        BigC = Math.pow(c, 2) - 3 * b * d;
        if (BigA == 0 && BigB == 0) {
            x = -3 * d / c;
        } else if (Math.pow(BigB, 2) - 4 * BigA * BigC > 0) {
            Y1 = BigA * b + 3 * a * ((-BigB + Math.pow((Math.pow(BigB, 2) - 4 * BigA * BigC), 0.5)) / 2);
            Y2 = BigA * b + 3 * a * ((-BigB - Math.pow((Math.pow(BigB, 2) - 4 * BigA * BigC), 0.5)) / 2);
            if (Y1 > 0 && Y2 > 0) {
                x = (-b - (Math.pow(Y1, (1 / 3.0)) + Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            } else if (Y1 > 0 && Y2 < 0) {
                Y2 = Math.abs(Y2);
                x = (-b - (Math.pow(Y1, (1 / 3.0)) - Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            } else if (Y1 < 0 && Y2 > 0) {
                Y1 = Math.abs(Y1);
                x = (-b - (-Math.pow(Y1, (1 / 3.0)) + Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            } else if (Y1 < 0 && Y2 < 0) {
                Y2 = Math.abs(Y2);
                Y1 = Math.abs(Y1);
                x = (-b + (Math.pow(Y1, (1 / 3.0)) + Math.pow(Y2, (1 / 3.0)))) / (3 * a);
            }
        } else if (Math.pow(BigB, 2) - 4 * BigA * BigC == 0 && BigA != 0) {
            K = BigB / BigA;
            x1 = -b / a + K;
            x2 = -K / 2;
            x3 = -K / 2;
            if (x1 <= x2) {
                x = x2;
            } else if (x2 <= x1) {
                x = x1;
            }
        } else if (Math.pow(BigB, 2) - 4 * BigA * BigC < 0 && BigA > 0) {
            T = (2 * BigA * b - 3 * a * BigB) / (2 * Math.pow((Math.pow(BigA, 3)), 0.5));
            System.out.println(T);
            theta = Math.acos(T);
            x1 = (-b - 2 * Math.pow(BigA, 0.5) * Math.cos(theta / 3)) / (3 * a);
            x2 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3) + Math.pow(3, 0.5) * Math.sin(theta / 3))) / (3 * a);
            x3 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3) - Math.pow(3, 0.5) * Math.sin(theta / 3))) / (3 * a);
            if (x1 <= x2) {
                x = x2;
            } else if (x2 < x1) {
                x = x1;
            }
            if (x <= x3) {
                x = x3;
            }
        }
        return x;

    }

    public static double Button1_Click(double T, double aw) {
        double fi, phi, N, Q, M, z, a, b, c, AA, BB, CC, Pc, Tc, Tr, P0, P, cha0, R, ksic, F, Xi, Yi, Zi, ai, bi, ci, alpha, omegaa, omegab, omegac;
        double PP, faw, fiT0, fp, fi0, CCi, thet, xxi, cha;
        double i, a1, b1, c1, d1;

        Pc = 4600000;
        Tc = 190.4;
        Tr = T / Tc;  // T是输入的温度
        P0 = 20;
        P = 0.8 * P0;
        cha0 = 0;
        R = 8.314;
        ksic = 0.324;
        F = 0.455336;
        Xi = 2.3048 * Math.pow(10, (-6));
        Yi = 2752.29;
        Zi = 23.01;
        ai = 1.5844 * Math.pow(10, 13);
        bi = -6591.43;
        ci = 27.04;
        while (true) {

            a1 = 1;
            b1 = 2 - 3 * ksic;
            c1 = 3 * Math.pow(ksic, 2);
            d1 = -Math.pow(ksic, 3);
            omegab = L_Z(a1, b1, c1, d1);
            omegac = 1 - 3 * ksic;
            omegaa = 3 * Math.pow(ksic, 2) + 3 * (1 - 2 * ksic) * omegab + Math.pow(omegab, 2) + 1 - 3 * ksic;
            alpha = Math.pow((1 + F * (1 - Math.pow(Tr, 0.5))), 2);
            a = omegaa * alpha * Math.pow(R, 2) * Math.pow(Tc, 2) / Pc;
            b = omegab * R * Tc / Pc;
            c = omegac * R * Tc / Pc;
            AA = a * P * Math.pow(10, 5) / (Math.pow(R, 2) * Math.pow(T, 2));
            BB = b * P * Math.pow(10, 5) / (R * T);
            CC = c * P * Math.pow(10, 5) / (R * T);
            z = V_Z(1, CC - 1, AA - 2 * BB * CC - BB * BB - BB - CC, (BB * CC + CC - AA) * BB);
            N = Math.pow((b * c + Math.pow((0.5 * (b + c)), 2)), 0.5);
            Q = (0.5 * (b + c) + N) * P * Math.pow(10, 5) / (R * T);
            M = (0.5 * (b + c) - N) * P * Math.pow(10, 5) / (R * T);
            phi = Math.exp((z - 1) - Math.log(z - BB) + a * Math.log((z + M) / (z + Q)) / (2 * N * R * T));
            fi = phi * P;
            faw = Math.pow(aw, (-23 / 3.0));
            fiT0 = ai * Math.exp(bi / (T - ci));
            fp = Math.exp(0.4242 * P / T);
            fi0 = fiT0 * fp * faw;
            CCi = Xi * Math.exp(Yi / (T - Zi));
            thet = CCi * fi / (1 + fi * CCi);
            xxi = fi / (fi0 * Math.pow((1 - thet), (1 / 3.0)));
            cha = Math.abs(xxi - 1);
            if (cha < 0.0001) {
                break;
            } else {
                PP = P;
                P = P - (P - P0) * cha / (cha - cha0);
                P0 = PP;
                cha0 = cha;
            }
        }
        return P / 10;
    }

    public static void main(String... org) {
        System.out.println("开始");
        System.out.println("" + Test.Button1_Click(10, 1));
        System.out.println("结束");
    }
}
