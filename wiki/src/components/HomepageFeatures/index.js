import clsx from "clsx";
import Heading from "@theme/Heading";
import styles from "./styles.module.css";

const FeatureList = [
    {
        title: "复杂难用",
        Svg: require("@site/static/img/undraw_docusaurus_mountain.svg").default,
        description: <>一般人学不会的</>,
    },
    {
        title: "文档简略",
        Svg: require("@site/static/img/undraw_docusaurus_tree.svg").default,
        description: <>根本不想写文档</>,
    },
    {
        title: "没有功能",
        Svg: require("@site/static/img/undraw_docusaurus_react.svg").default,
        description: <>连个功能都没有</>,
    },
];

function Feature({Svg, title, description}) {
    return (
        <div className={clsx("col col--4")}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img"/>
            </div>
            <div className="text--center padding-horiz--md">
                <Heading as="h3">{title}</Heading>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures() {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
