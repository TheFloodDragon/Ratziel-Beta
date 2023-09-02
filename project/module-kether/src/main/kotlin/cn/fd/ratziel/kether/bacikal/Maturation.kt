package cn.fd.ratziel.kether.bacikal

/**
 * @author Lanscarlos
 * @since 2023-08-21 17:13
 */
class Maturation {
    interface M0<CTX, R> {
        fun apply(context: CTX): R
    }

    interface M1<CTX, S1, R> {
        fun apply(context: CTX, s1: S1): R
    }

    interface M2<CTX, S1, S2, R> {
        fun apply(context: CTX, s1: S1, s2: S2): R
    }

    interface M3<CTX, S1, S2, S3, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3): R
    }

    interface M4<CTX, S1, S2, S3, S4, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4): R
    }

    interface M5<CTX, S1, S2, S3, S4, S5, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4, s5: S5): R
    }

    interface M6<CTX, S1, S2, S3, S4, S5, S6, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4, s5: S5, s6: S6): R
    }

    interface M7<CTX, S1, S2, S3, S4, S5, S6, S7, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4, s5: S5, s6: S6, s7: S7): R
    }

    interface M8<CTX, S1, S2, S3, S4, S5, S6, S7, S8, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4, s5: S5, s6: S6, s7: S7, s8: S8): R
    }

    interface M9<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4, s5: S5, s6: S6, s7: S7, s8: S8, s9: S9): R
    }

    interface M10<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, R> {
        fun apply(context: CTX, s1: S1, s2: S2, s3: S3, s4: S4, s5: S5, s6: S6, s7: S7, s8: S8, s9: S9, s10: S10): R
    }

    interface M11<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, R> {
        fun apply(
            context: CTX,
            s1: S1,
            s2: S2,
            s3: S3,
            s4: S4,
            s5: S5,
            s6: S6,
            s7: S7,
            s8: S8,
            s9: S9,
            s10: S10,
            s11: S11,
        ): R
    }

    interface M12<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, R> {
        fun apply(
            context: CTX,
            s1: S1,
            s2: S2,
            s3: S3,
            s4: S4,
            s5: S5,
            s6: S6,
            s7: S7,
            s8: S8,
            s9: S9,
            s10: S10,
            s11: S11,
            s12: S12,
        ): R
    }

    interface M13<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, R> {
        fun apply(
            context: CTX,
            s1: S1,
            s2: S2,
            s3: S3,
            s4: S4,
            s5: S5,
            s6: S6,
            s7: S7,
            s8: S8,
            s9: S9,
            s10: S10,
            s11: S11,
            s12: S12,
            s13: S13,
        ): R
    }

    interface M14<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, R> {
        fun apply(
            context: CTX,
            s1: S1,
            s2: S2,
            s3: S3,
            s4: S4,
            s5: S5,
            s6: S6,
            s7: S7,
            s8: S8,
            s9: S9,
            s10: S10,
            s11: S11,
            s12: S12,
            s13: S13,
            s14: S14,
        ): R
    }

    interface M15<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15, R> {
        fun apply(
            context: CTX,
            s1: S1,
            s2: S2,
            s3: S3,
            s4: S4,
            s5: S5,
            s6: S6,
            s7: S7,
            s8: S8,
            s9: S9,
            s10: S10,
            s11: S11,
            s12: S12,
            s13: S13,
            s14: S14,
            s15: S15,
        ): R
    }

    interface M16<CTX, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15, S16, R> {
        fun apply(
            context: CTX,
            s1: S1,
            s2: S2,
            s3: S3,
            s4: S4,
            s5: S5,
            s6: S6,
            s7: S7,
            s8: S8,
            s9: S9,
            s10: S10,
            s11: S11,
            s12: S12,
            s13: S13,
            s14: S14,
            s15: S15,
            s16: S16,
        ): R
    }
}

