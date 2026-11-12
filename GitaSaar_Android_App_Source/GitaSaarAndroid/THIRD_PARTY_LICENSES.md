# Third-party content and licenses

## Bhagavad Gita Dataset — Adiyogi Arts

Source: https://github.com/AdiyogiArts/gita-json

MIT License

Copyright (c) 2024 Adiyogi Arts

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

The original Bhagavad Gita is a traditional sacred scripture. Hindi meaning
text is fetched from the upstream dataset; any editorial review of accuracy
and individual rights remains the responsibility of the app publisher.

## Public-domain Krishna–Arjuna paintings (bundled as high-resolution JPEGs)

These three illustrations are fetched only during the GitHub Actions build and
bundled into the application. No artwork is downloaded from third-party sites
during app use.

1. `krishna_chariot.jpg`: "Arjuna and His Charioteer Krishna Confront Karna"
   (circa 1820), unknown Indian artist, Philadelphia Museum of Art.
   Commons page: https://commons.wikimedia.org/wiki/File:Arjuna_and_His_Charioteer_Krishna_Confront_Karna.jpg
   Public-domain historical painting / PD-Art.
2. `krishna_teaching.jpg`: "Krishna Arjuna Gita" (circa 1830), unknown
   artist, British Museum. Commons page:
   https://commons.wikimedia.org/wiki/File:Krishna_Arjuna_Gita.jpg
   Public-domain historical painting / PD-Art.
3. `krishna_battle.jpg`: "Krishna instructing Arjuna", illustration by
   Surendra Nath Kar, from *Myths of the Hindus & Buddhists* (1914).
   Commons page: https://commons.wikimedia.org/wiki/File:Krishna_instructing_Arjuna.jpg
   Public-domain 1914 illustration, subject to jurisdictional review.

## Dataset normalization

The Adiyogi Arts upstream source includes 701 keyed entries because it numbers
the optional opening question by Arjuna as Bhagavad Gita 13.1. To match the
commonly published 700-verse edition, this project preserves the verses
following that introductory question, moves 13.2 to 13.1 and so on through
13.35 to 13.34, and stores the source commentary with each corresponding
verse. The omission/numbering is editorial and should be disclosed to users.
