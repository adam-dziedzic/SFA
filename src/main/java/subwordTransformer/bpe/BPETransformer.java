package subwordTransformer.bpe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import subwordTransformer.UnsupervisedTransformer;

/**
 * A transformer that uses byte pair encoding to find long representative
 * subsequences.
 */
public class BPETransformer extends UnsupervisedTransformer<BPEParameter> {

  private Map<List<List<Short>>, Integer> vocab;
  private List<List<List<Short>>> merges;

  private List<List<Short>> bestPair;
  private int bestCount;

  /**
   * @param alphabetSize        the alphabet size of the input words
   * @param positionalAlphabets whether the transformer should use positional
   *                            alphabets, i.e. different positions in words have
   *                            different meanings
   */
  public BPETransformer(int alphabetSize, boolean positionalAlphabets) {
    super(alphabetSize, positionalAlphabets);
  }

  /**
   * @param alphabetSize        the alphabet size of the input words
   * @param positionalAlphabets whether the transformer should use positional
   *                            alphabets, i.e. different positions in words have
   *                            different meanings
   * @param fillCharacter       the character to be used for wildcards (default:
   *                            -1)
   */
  public BPETransformer(int alphabetSize, boolean positionalAlphabets, short fillCharacter) {
    super(alphabetSize, positionalAlphabets, fillCharacter);
  }

  @Override
  public int getOutputAlphabetSize() {
    if (this.hasPositionalAlphabets()) {
      return this.getInputAlphabetSize() + 1;
    } else {
      return this.getInputAlphabetSize();
    }
  }

  @Override
  protected void buildDictionary() {
    // build vocab from words
    vocab = BPEUtils.buildVocab(this.getWords(), this.hasPositionalAlphabets(), this.getInputAlphabetSize());
    // now find merges
    merges = new ArrayList<>();
    this.findMerges(false);
  }

  @Override
  protected void updateDictionary() {
    if (this.getParameter().getMinSupport() <= this.getOldParameter().getMinSupport()) {
      this.findMerges(true);
    } else {
      this.buildDictionary();
    }
  }

  private void findMerges(boolean continueSearch) {
    int minCount = (int) Math.ceil(this.getWords().length * this.getParameter().getMinSupport());
    while (true) {
      if (!continueSearch) {
        Map<List<List<Short>>, Integer> pairs = BPEUtils.getStats(this.vocab);
        // find most frequent pair
        bestPair = null;
        bestCount = -1;
        for (Entry<List<List<Short>>, Integer> e : pairs.entrySet()) {
          if (e.getValue() > bestCount) {
            bestPair = e.getKey();
            bestCount = e.getValue();
          }
        }
      } else {
        continueSearch = false;
      }
      if (bestCount >= minCount) {
        merges.add(bestPair);
        this.mergeVocab(bestPair);
      } else {
        break;
      }
    }
  }

  private void mergeVocab(List<List<Short>> pair) {
    List<Short> mergedPair = BPEUtils.getMergedPair(pair);
    vocab = BPEUtils.mergeVocab(vocab, pair, mergedPair);
  }

  @Override
  public short[][] transform(short[] word) {
    return BPEUtils.transform(word, merges, this.hasPositionalAlphabets(), this.getInputAlphabetSize(), this.getFillCharacter());
  }

}