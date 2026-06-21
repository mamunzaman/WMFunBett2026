gsap.registerPlugin(ScrollTrigger);

const jackpotEl = document.querySelector("[data-jackpot-value]");
const roundsEl = document.querySelector('[data-stat="rounds"]');
const gamesEl = document.querySelector('[data-stat="games"]');
const heroTitle = document.querySelector(".hero__title");
const heroEyebrow = document.querySelector(".hero__eyebrow");
const heroSubtitle = document.querySelector(".hero__subtitle");
const jackpotCard = document.querySelector(".jackpot__card");
const matchCards = gsap.utils.toArray(".match-card");

const jackpotTarget = Number(jackpotEl?.dataset.jackpotValue ?? 0);

function formatEuro(value) {
  return `€ ${Math.round(value).toLocaleString("de-DE")}`;
}

function runIntro(reduceMotion) {
  if (reduceMotion) {
    jackpotEl.textContent = formatEuro(jackpotTarget);
    roundsEl.textContent = "3";
    gamesEl.textContent = "5";
    gsap.set([heroEyebrow, heroTitle, heroSubtitle, jackpotCard], { autoAlpha: 1, y: 0 });
    gsap.set(matchCards, { autoAlpha: 1, y: 0 });
    return;
  }

  gsap.set([heroEyebrow, heroTitle, heroSubtitle, jackpotCard], { autoAlpha: 0, y: 24 });
  gsap.set(matchCards, { autoAlpha: 0, y: 40 });

  const intro = gsap.timeline({ defaults: { ease: "power3.out" } });

  intro
    .to(heroEyebrow, { autoAlpha: 1, y: 0, duration: 0.5 })
    .to(heroTitle, { autoAlpha: 1, y: 0, duration: 0.65 }, "-=0.25")
    .to(heroSubtitle, { autoAlpha: 1, y: 0, duration: 0.5 }, "-=0.35")
    .to(jackpotCard, { autoAlpha: 1, y: 0, duration: 0.7, ease: "back.out(1.4)" }, "-=0.2");

  const counters = { jackpot: 0, rounds: 0, games: 0 };
  intro.to(
    counters,
    {
      jackpot: jackpotTarget,
      rounds: 3,
      games: 5,
      duration: 1.6,
      ease: "power2.out",
      onUpdate: () => {
        jackpotEl.textContent = formatEuro(counters.jackpot);
        roundsEl.textContent = String(Math.round(counters.rounds));
        gamesEl.textContent = String(Math.round(counters.games));
      },
    },
    "-=0.35"
  );
}

function runScrollReveal(reduceMotion) {
  if (reduceMotion) return;

  ScrollTrigger.batch(matchCards, {
    start: "top 88%",
    once: true,
    onEnter: (batch) => {
      gsap.from(batch, {
        autoAlpha: 0,
        y: 48,
        scale: 0.96,
        duration: 0.65,
        stagger: 0.12,
        ease: "power3.out",
        overwrite: true,
      });
    },
  });
}

const mm = gsap.matchMedia();

mm.add(
  {
    reduceMotion: "(prefers-reduced-motion: reduce)",
  },
  (context) => {
    const { reduceMotion } = context.conditions;
    runIntro(reduceMotion);
    runScrollReveal(reduceMotion);

    return () => {
      ScrollTrigger.getAll().forEach((t) => t.kill());
    };
  }
);
