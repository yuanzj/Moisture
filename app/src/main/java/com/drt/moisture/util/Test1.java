package com.drt.moisture.util;

public class Test1 {

    public Test1() {
    }

    public static double aw(double a, double x, double T) {
        double aw = 0.0;
        if (a == 0.0) {
            aw = 1.0;
        }

        if (a == 1.0) {
            aw = -2.0E-4 * Math.pow(x, 2.0) - 0.003 * x + 0.9735;
        }

        if (a == 2.0) {
            aw = -5.0E-5 * Math.pow(x, 2.0) - 0.0047 * x + 0.9807;
        }

        if (a == 3.0) {
            aw = -3.0E-4 * Math.pow(x, 2.0) - 2.0E-4 * x + 0.986;
        }

        if (a == 4.0) {
            aw = -1.0E-4 * Math.pow(x, 2.0) - 9.0E-4 * x + 0.9579;
        }

        if (a == 5.0) {
            aw = -1.0E-4 * Math.pow(x, 2.0) - 0.0039 * x + 0.9343;
        }

        if (a == 6.0) {
            double a1 = -5.8466;
            double a2 = 26.5022;
            double b1 = -2.8226;
            double b2 = 19.8439;
            double AA = a1 + a2 * 0.001 * T;
            x /= 100.0;
            double BB = b1 + b2 * 0.001 * T;
            double xw = 1.0 - x / 32.0 / (x / 32.0 + (1.0 - x) / 18.0);
            aw = xw * Math.exp((1.0 - xw) * (1.0 - xw) * (AA + 2.0 * (AA - BB) * xw));
        }

        if (a == 7.0) {
            aw = 3.0E-4 * Math.pow(x, 2.0) - 0.0236 * x + 1.0249;
        }

        if (a == 8.0) {
            aw = -8.0E-5 * Math.pow(x, 2.0) - 0.005 * x + 0.9998;
        }

        if (a == 9.0) {
            aw = -9.0E-5 * Math.pow(x, 2.0) - 0.0017 * x + 0.9848;
        }

        if (a == 10.0) {
            aw = -7.0E-5 * Math.pow(x, 2.0) - 0.0014 * x + 0.9945;
        }

        if (a == 11.0) {
            aw = -4.0E-5 * Math.pow(x, 2.0) - 0.0018 * x + 0.9956;
        }

        return aw;
    }

    public static double[] Ci(double T) {
        double[] X = new double[]{2.3048 * Math.pow(10.0, -6.0), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.3151 * Math.pow(10.0, -6.0), 1.6464 * Math.pow(10.0, -6.0)};
        double[] Y = new double[]{2752.29, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2427.37, 2799.66};
        double[] Z = new double[]{23.01, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.64, 15.9};
        double[] Ci = new double[X.length];

        for (int i = 0; i < X.length; ++i) {
            Ci[i] = X[i] * Math.exp(Y[i] / (T - Z[i]));
        }

        return Ci;
    }

    public static double L_Z(double a, double b, double c, double d) {
        double x = 0.0;
        double BigA = Math.pow(b, 2.0) - 3.0 * a * c;
        double BigB = b * c - 9.0 * a * d;
        double BigC = Math.pow(c, 2.0) - 3.0 * b * d;
        if (BigA == 0.0 && BigB == 0.0) {
            x = -3.0 * d / c;
        } else if (Math.pow(BigB, 2.0) - 4.0 * BigA * BigC > 0.0) {
            double Y1 = BigA * b + 3.0 * a * ((-BigB + Math.pow(Math.pow(BigB, 2.0) - 4.0 * BigA * BigC, 0.5)) / 2.0);
            double Y2 = BigA * b + 3.0 * a * ((-BigB - Math.pow(Math.pow(BigB, 2.0) - 4.0 * BigA * BigC, 0.5)) / 2.0);
            if (Y1 > 0.0 && Y2 > 0.0) {
                x = (-b - (Math.pow(Y1, 0.3333333333333333) + Math.pow(Y2, 0.3333333333333333))) / (3.0 * a);
            } else if (Y1 > 0.0 && Y2 < 0.0) {
                Y2 = Math.abs(Y2);
                x = (-b - (Math.pow(Y1, 0.3333333333333333) - Math.pow(Y2, 0.3333333333333333))) / (3.0 * a);
            } else if (Y1 < 0.0 && Y2 > 0.0) {
                Y1 = Math.abs(Y1);
                x = (-b - (-Math.pow(Y1, 0.3333333333333333) + Math.pow(Y2, 0.3333333333333333))) / (3.0 * a);
            } else if (Y1 < 0.0 && Y2 < 0.0) {
                Y2 = Math.abs(Y2);
                Y1 = Math.abs(Y1);
                x = (-b + Math.pow(Y1, 0.3333333333333333) + Math.pow(Y2, 0.3333333333333333)) / (3.0 * a);
            }
        } else {
            double x1;
            double x2;
            double x3;
            if (Math.pow(BigB, 2.0) - 4.0 * BigA * BigC == 0.0 && BigA != 0.0) {
                double K = BigB / BigA;
                x1 = -b / a + K;
                x2 = -K / 2.0;
                x3 = -K / 2.0;
                if (x1 >= x2) {
                    x = x2;
                } else if (x2 >= x1) {
                    x = x1;
                }
            } else if (Math.pow(BigB, 2.0) - 4.0 * BigA * BigC < 0.0 && BigA > 0.0) {
                double T = (2.0 * BigA * b - 3.0 * a * BigB) / (2.0 * Math.pow(Math.pow(BigA, 3.0), 0.5));
                double theta = Math.acos(T);
                x1 = (-b - 2.0 * Math.pow(BigA, 0.5) * Math.cos(theta / 3.0)) / (3.0 * a);
                x2 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3.0) + Math.pow(3.0, 0.5) * Math.sin(theta / 3.0))) / (3.0 * a);
                x3 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3.0) - Math.pow(3.0, 0.5) * Math.sin(theta / 3.0))) / (3.0 * a);
                if (x1 >= x2) {
                    x = x2;
                } else if (x2 > x1) {
                    x = x1;
                }

                if (x >= x3) {
                    x = x3;
                }
            }
        }

        return x;
    }

    public static double V_Z(double a, double b, double c, double d) {
        double x = 0.0;
        double BigA = Math.pow(b, 2.0) - 3.0 * a * c;
        double BigB = b * c - 9.0 * a * d;
        double BigC = Math.pow(c, 2.0) - 3.0 * b * d;
        if (BigA == 0.0 && BigB == 0.0) {
            x = -3.0 * d / c;
        } else if (Math.pow(BigB, 2.0) - 4.0 * BigA * BigC > 0.0) {
            double Y1 = BigA * b + 3.0 * a * ((-BigB + Math.pow(Math.pow(BigB, 2.0) - 4.0 * BigA * BigC, 0.5)) / 2.0);
            double Y2 = BigA * b + 3.0 * a * ((-BigB - Math.pow(Math.pow(BigB, 2.0) - 4.0 * BigA * BigC, 0.5)) / 2.0);
            if (Y1 > 0.0 && Y2 > 0.0) {
                x = (-b - (Math.pow(Y1, 0.3333333333333333) + Math.pow(Y2, 0.3333333333333333))) / (3.0 * a);
            } else if (Y1 > 0.0 && Y2 < 0.0) {
                Y2 = Math.abs(Y2);
                x = (-b - (Math.pow(Y1, 0.3333333333333333) - Math.pow(Y2, 0.3333333333333333))) / (3.0 * a);
            } else if (Y1 < 0.0 && Y2 > 0.0) {
                Y1 = Math.abs(Y1);
                x = (-b - (-Math.pow(Y1, 0.3333333333333333) + Math.pow(Y2, 0.3333333333333333))) / (3.0 * a);
            } else if (Y1 < 0.0 && Y2 < 0.0) {
                Y2 = Math.abs(Y2);
                Y1 = Math.abs(Y1);
                x = (-b + Math.pow(Y1, 0.3333333333333333) + Math.pow(Y2, 0.3333333333333333)) / (3.0 * a);
            }
        } else {
            double x1;
            double x2;
            double x3;
            if (Math.pow(BigB, 2.0) - 4.0 * BigA * BigC == 0.0 && BigA != 0.0) {
                double K = BigB / BigA;
                x1 = -b / a + K;
                x2 = -K / 2.0;
                x3 = -K / 2.0;
                if (x1 <= x2) {
                    x = x2;
                } else if (x2 <= x1) {
                    x = x1;
                }
            } else if (Math.pow(BigB, 2.0) - 4.0 * BigA * BigC < 0.0 && BigA > 0.0) {
                double T = (2.0 * BigA * b - 3.0 * a * BigB) / (2.0 * Math.pow(Math.pow(BigA, 3.0), 0.5));
                double theta = Math.acos(T);
                x1 = (-b - 2.0 * Math.pow(BigA, 0.5) * Math.cos(theta / 3.0)) / (3.0 * a);
                x2 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3.0) + Math.pow(3.0, 0.5) * Math.sin(theta / 3.0))) / (3.0 * a);
                x3 = (-b + Math.pow(BigA, 0.5) * (Math.cos(theta / 3.0) - Math.pow(3.0, 0.5) * Math.sin(theta / 3.0))) / (3.0 * a);
                if (x1 <= x2) {
                    x = x2;
                } else if (x2 < x1) {
                    x = x1;
                }

                if (x <= x3) {
                    x = x3;
                }
            }
        }

        return x;
    }

    public static double phi(double[] y, double P, double T) {
        double[] phi = new double[y.length];
        double[] Tc = new double[]{190.6};
        double[] Pc = new double[]{46.0};
        double[] w = new double[]{0.008};
        double[] Tr = new double[y.length];
        double[] Pr = new double[y.length];
        double[] ai = new double[y.length];
        double[] bi = new double[y.length];
        double R = 8.314;
        double a = 0.0;
        double b = 0.0;
        double x = 0.0;

        int i;
        for (i = 0; i < y.length; ++i) {
            Tr[i] = T / Tc[i];
            Pr[i] = P / Pc[i];
            bi[i] = 0.08664 * R * Tc[i] / Pc[i];
            b += y[i] * bi[i];
            ai[i] = 0.42747 * R * R * Tc[i] * Tc[i] / Pc[i] * Math.pow(1.0 + (0.48 + 1.574 * w[i] - 0.176 * w[i] * w[i]) * (1.0 - Math.pow(Tr[i], 0.5)), 2.0);
        }

        for (i = 0; i < y.length; ++i) {
            a += y[i] * ai[i];
        }

        double A = a * P / (R * R * T * T);
        double B = b * P / (R * T);
        double za = 1.0;
        double zb = -1.0;
        double zc = A - B - B * B;
        double zd = -A * B;
        double ZV = V_Z(za, zb, zc, zd);

        for (i = 0; i < y.length; ++i) {
            double sum = 0.0;

            for (int j = 0; j < y.length; ++j) {
                sum += y[j] * ai[j];
            }

            phi[i] = Math.exp(bi[i] * (ZV - 1.0) / b - Math.log(ZV - B) + A * (bi[i] / b - 2.0 * sum / a) * Math.log(1.0 + B / ZV) / B);
        }

        for (i = 0; i < y.length; ++i) {
            x += y[i] * Math.log(phi[i]);
        }

        x = Math.exp(x);
        return x;
    }

    public static double[] SIfi0(double aw, double P, double T) {
        double[] fi0 = new double[11];
        double[] fiT = new double[11];
        double[] a = new double[]{1.5844 * Math.pow(10.0, 13.0), 4.75 * Math.pow(10.0, 11.0), 1.0 * Math.pow(10.0, 12.0), 1.0 * Math.pow(10.0, 10.0), 1.0 * Math.pow(10.0, 10.0), 1.0, 1.0, 1.0, 1.0, 9.7939 * Math.pow(10.0, 11.0), 4.63726 * Math.pow(10.0, 10.0)};
        double[] b = new double[]{-6591.43, -5465.6, -5400.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -5286.59, -4004.18};
        double[] c = new double[]{27.04, 57.93, 55.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 31.65, 90.67};

        for (int i = 0; i < b.length; ++i) {
            fiT[i] = a[i] * Math.exp(b[i] / (T - c[i]));
        }

        double fp = Math.exp(0.4242 * P / T);
        double faw = Math.pow(aw, -7.67);

        for (int i = 0; i < a.length; ++i) {
            fi0[i] = fp * faw * fiT[i];
        }

        return fi0;
    }

    public static double GetP(double t, double[] y, double aw) {
        double sum1 = 0.0;
        double sum2 = 0.0;
        double sum3 = 0.0;
        double sum4 = 0.0;
        double cha0 = 0.0;
        double s1P = 20.0;
        double P0 = s1P / 0.8;
        double T = t + 273.15;

        int i;
        for (i = 0; i < y.length; ++i) {
            sum4 += y[i];
        }

        for (i = 0; i < y.length; ++i) {
            y[i] /= sum4;
        }

        double[] fi = new double[y.length];
        double[] fi0 = new double[y.length];
        double[] Ci = new double[y.length];
        double[] thet = new double[y.length];
        double[] xi = new double[y.length];
        if (y[0] == 1.0) {
            i = 0;

            while (true) {
                sum1 = 0.0;
                sum2 = 0.0;
                sum3 = 0.0;
                double phi = phi(y, s1P, T);

                int j;
                for (j = 0; j < y.length; ++j) {
                    fi[j] = phi * s1P * y[j];
                }

                fi0 = SIfi0(aw, s1P, T);
                Ci = Ci(T);

                for (j = 0; j < y.length; ++j) {
                    sum1 += Ci[j] * fi[j];
                }

                for (j = 0; j < y.length; ++j) {
                    thet[j] = Ci[j] * fi[j] / (1.0 + sum1);
                }

                for (j = 0; j < y.length; ++j) {
                    sum2 += thet[j];
                }

                for (j = 0; j < y.length; ++j) {
                    xi[j] = fi[j] / (fi0[j] * Math.pow(1.0 - sum2, 0.3333333333333333));
                }

                for (j = 0; j < y.length; ++j) {
                    sum3 += xi[j];
                }

                double cha = Math.abs(sum3 - 1.0);
                if (cha < 1.0E-4) {
                    break;
                }

                double PP = s1P;
                s1P -= (s1P - P0) * cha / (cha - cha0);
                P0 = PP;
                cha0 = cha;
                ++i;
            }
        }

        return s1P / 10.0;
    }

    public static void main(String[] panduan) {
        double[] y = new double[]{100.0};
        double aw = 1.0;
        double T = 0.0;
        double P = GetP(T, y, aw);
        System.out.println(String.format("%.3f", P) + "MPa");
    }

}
