package com.drt.moisture.util;

public class BarometricPressureUtil {


    /**
     * 输入温度、活度 获取压力（单位MPa）
     *
     * @param t1 t1为输入的温度℃
     * @param aw aw为输入的活度
     */
    public static double Button111_Click(double t1, double aw) {

        double fi, phi, N, Q, M, z, T, p1, p2, a, a2, b, c, AA, BB, CC, Pc, Tc, Tr, P0, P, cha0, R, ksic, F, Xi, Yi, Zi, ai, bi, ci, alpha, omegaa, omegab, omegac;
        double PP, faw, fiT0, fp, fi0, CCi, thet, xxi, cha;
        double a1, b1, c1, d1;

        //  ' 输入温度T、孔隙半径R、初始压力P0
        T = t1 + 273.15;//             ' 将输入温度℃转化为计算所需要的温度单位T
        Pc = 4600000;    //            ' Pc为临界压力是常数
        Tc = 190.4;        //           ' Tc为临界温度是常数
        Tr = T / Tc;       //         ' T是输入的温度转换后的值
        P0 = 20;//                   ' 设置P0初始压力
        P = 0.8 * P0;
        R = 8.314;//                     ' R为孔隙半径是常数

        //  设置初始值
        cha0 = 0;
        ksic = 0.324;//                  ' ksic是由表3-2给出的经验参数
        F = 0.455336;
        Xi = 2.3048 * Math.pow(10, (-6));
        Yi = 2752.29;
        Zi = 23.01;
        ai = 1.5844 * Math.pow(10, 13);
        bi = -6591.43;
        ci = 27.04;

        for (int i = 1; i < 1000000000; i++) {        //设置循环

            // 式(3-9)
            // ζc用ksic表示，Ωa用omegaa表示 Ωb用omegab表示 Ωc用omegac表示
            a1 = 1;
            b1 = 2 - 3 * ksic;
            c1 = 3 * Math.pow(ksic, 2);
            d1 = -Math.pow(ksic, 3);
            omegab = L_Z(a1, b1, c1, d1);//       ' omegab 为式3-9的最小正根pg13
            omegac = 1 - 3 * ksic;//             ' 式3-7
            omegaa = 3 * Math.pow(ksic, 2) + 3 * (1 - 2 * ksic) * omegab + Math.pow(omegab, 2) + 1 - 3 * ksic;//        ' 式3-8

            //        ' 式 (3-10)
            //        ' α用alpha表示
            alpha = Math.pow((1 + F * (1 - Math.pow(Tr, 0.5))), 2);

            //        ' 式(3-6)
            a = omegaa * alpha * Math.pow(R, 2) * Math.pow(Tc, 2) / Pc;
            b = omegab * R * Tc / Pc;
            c = omegac * R * Tc / Pc;

            //        ' 式(3-5)
            AA = a * P * Math.pow(10, 5) / (Math.pow(R, 2) * Math.pow(T, 2));
            BB = b * P * Math.pow(10, 5) / (R * T);
            CC = c * P * Math.pow(10, 5) / (R * T);
            z = V_Z(1, CC - 1, AA - 2 * BB * CC - BB * BB - BB - CC, (BB * CC + CC - AA) * BB);

            //        ' 式(3-12)
            N = Math.pow((b * c + Math.pow((0.5 * (b + c)), 2)), 0.5);
            Q = (0.5 * (b + c) + N) * P * Math.pow(10, 5) / (R * T);
            M = (0.5 * (b + c) - N) * P * Math.pow(10, 5) / (R * T);

            //         式(3-11)
            //        Φ用phi表示
            phi = Math.exp((z - 1) - Math.log(z - BB) + a * Math.log((z + M) / (z + Q)) / (2 * N * R * T));

            //    ' 式(3-13)
            //        ' 气体逸度用fi表示
            fi = phi * P;

            //       ' 计算fi0和θ0（pg15-3.4节）
            faw = Math.pow(aw, (-23 / 3.0));//                 ' 式3-19
            fiT0 = ai * Math.exp(bi / (T - ci));//        ' 式3-20
            fp = Math.exp(0.4242 * P / T);//             ' 式3-18
            fi0 = fiT0 * fp * faw;//              ' 式3-17
            CCi = Xi * Math.exp(Yi / (T - Zi));//       ' 式3-21
            //       ' 填充率用thet表示
            thet = CCi * fi / (1 + fi * CCi);//      ' 式3-22

            //     ' // 计算摩尔分数 xi（pg16-3.5节）
            xxi = fi / (fi0 * Math.pow((1 - thet), (1 / 3.0)));//  ' 式3-23
            //        ' xi与1的差用cha表示 为计算精度
            cha = Math.abs(xxi - 1);

            if (cha < 0.0001) {
                break;
            } else {
                PP = P;
                P = P - (P - P0) * cha / (cha - cha0);//    ' 式3-2
                P0 = PP;
                cha0 = cha;

            }


        }
        //' 结果输出，单位MPa
        return  P = P / 10;
    }


    /**
     * 定义函数L_Z
     * 定义求解一元三次方程最小根函数
     * 用于计算Ωb（参见论文pg13式3-9）
     **/
    public static double L_Z(double a, double b, double c, double d) {
        //  MIN
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


    /**
     * 定义函数V_Z
     * 定义求解一元三次方程最大根函数
     * 用于计算压缩因子z（参见论文pg12式3-4）
     *
     * @param a
     * @param b
     * @param c
     * @param d
     */
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

    public static void main(String... org) {
        System.out.println("开始");
        System.out.println("" + BarometricPressureUtil.Button111_Click(30, 1));
        System.out.println("结束");
    }


}
